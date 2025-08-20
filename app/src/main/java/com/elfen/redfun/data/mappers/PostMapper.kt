package com.elfen.redfun.data.mappers

import android.util.DisplayMetrics
import android.util.Patterns
import com.elfen.redfun.data.local.relations.FeedWithPost
import com.elfen.redfun.data.local.relations.PostWithMedia
import com.elfen.redfun.data.remote.models.Link
import com.elfen.redfun.domain.model.MediaImage
import com.elfen.redfun.domain.model.MediaVideo
import com.elfen.redfun.domain.model.Post
import kotlin.text.ifEmpty
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun PostWithMedia.asDomainModel() = Post(
    id = post.id,
    body = post.body,
    subreddit = post.subreddit,
    score = post.score,
    numComments = post.numComments,
    author = post.author,
    created = Instant.fromEpochSeconds(post.created),
    thumbnail = post.thumbnail,
    url = post.url,
    title = post.title,
    nsfw = post.nsfw,
    link = post.link,
    images = media.filter { it.isVideo == false || it.isVideo == null }.map {
        MediaImage(
            id = it.id,
            source = it.source,
            width = it.width,
            height = it.height,
            animated = true
        )
    },
    video = media.find { it.isVideo == true }.let {
        if (it == null) return@let null

        MediaVideo(
            source = it.source,
            width = it.width,
            height = it.height,
            duration = it.duration,
            isGif = it.isGif == true,
            fallback = it.fallback
        )
    },
    subredditIcon = post.subredditIcon
)

fun FeedWithPost.asDomainModel() = PostWithMedia(post = post, media = media).asDomainModel()

@OptIn(ExperimentalTime::class)
fun Link.asDomainModel(): Post {
    var images = emptyList<MediaImage>()
    var video: MediaVideo? = null

    if (mediaMetadata != null) {
        val first = mediaMetadata.values.first()
        if (first.hlsUrl === null) {
            val source = first.s?.u ?: first.s?.gif
            if (source != null)
                images = listOf(
                    MediaImage(
                        first.id!!,
                        source,
                        first.s!!.x,
                        first.s.y,
                        animated = first.s.gif != null
                    )
                )
        } else {
            video = MediaVideo(
                source = first.hlsUrl,
                width = first.x!!,
                height = first.y!!,
                duration = null,
                isGif = first.isGif == true,
                fallback = first.dashUrl
            )
        }
    }

    if (isVideo == true) {
        video = MediaVideo(
            source = media!!.redditVideo.hlsUrl,
            width = media.redditVideo.width,
            height = media.redditVideo.height,
            duration = media.redditVideo.duration,
            isGif = media.redditVideo.isGif,
            fallback = media.redditVideo.fallbackUrl,
        )
    }

    if (preview?.redditVideoPreview != null) {
        video = MediaVideo(
            source = preview.redditVideoPreview.hlsUrl!!,
            width = preview.redditVideoPreview.width,
            height = preview.redditVideoPreview.height,
            duration = null,
            isGif = false,
            fallback = null
        )
    }

    if (preview?.images?.isNotEmpty() == true && preview.enabled == true) {
        val imagesSorted = preview.images[0].resolutions.sortedByDescending { it.width }
        val image = imagesSorted.find {
            it.width > DisplayMetrics().widthPixels
        } ?: imagesSorted[0]

        images = listOf(
            MediaImage(
                preview.images[0].id,
                image.url,
                image.width, image.height,
                animated = preview.images[0].source.url.endsWith(".gif")
            )
        )
    }

    if (mediaMetadata != null && (preview?.enabled === null || preview.enabled)) {
        images = mediaMetadata.values.mapNotNull { media ->
            if (media.hlsUrl !== null || media.s == null) return@mapNotNull null

            MediaImage(
                source = media.s.u ?: media.s.gif ?: return@mapNotNull null,
                width = media.s.x,
                height = media.s.y,
                id = media.id!!,
                animated = true,
            )
        }
    }


    return Post(
        id = id,
        body = selftext,
        subreddit = subreddit,
        score = score,
        numComments = numComments,
        author = author,
        created = Instant.fromEpochSeconds(created),
        thumbnail = if (Patterns.WEB_URL.matcher(thumbnail).matches()) thumbnail else null,
        url = permalink,
        title = title,
        nsfw = over18,
        link = if (
            !isRedditMediaDomain &&
            urlOverriddenByDest?.isNotEmpty() === true &&
            mediaMetadata === null &&
            Patterns.WEB_URL.matcher(urlOverriddenByDest).matches()
        ) urlOverriddenByDest else null,
        images = images.ifEmpty { null },
        video = video,
        subredditIcon = subredditDetails.communityIcon?.ifEmpty { null } ?: subredditDetails.iconImg?.ifEmpty { null },
    )
}