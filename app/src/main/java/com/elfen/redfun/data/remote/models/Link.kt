@file:Suppress(
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "IMPLICIT_BOXING_IN_IDENTITY_EQUALS"
)

package com.elfen.redfun.data.remote.models

import android.util.DisplayMetrics
import android.util.Patterns
import com.elfen.redfun.domain.model.MediaImage
import com.elfen.redfun.domain.model.MediaVideo
import com.elfen.redfun.domain.model.Post
import com.google.gson.annotations.SerializedName
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Link(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subreddit") val subreddit: String,
    @SerializedName("author") val author: String,
    @SerializedName("score") val score: Int,
    @SerializedName("num_comments") val numComments: Int,
    @SerializedName("created") val created: Long,
    @SerializedName("is_reddit_media_domain") val isRedditMediaDomain: Boolean,
    @SerializedName("url_overridden_by_dest") val urlOverriddenByDest: String?,
    @SerializedName("removed_by_category") val removedByCategory: String?,
    @SerializedName("selftext") val selftext: String,
    @SerializedName("saved") val saved: Boolean,
    @SerializedName("over_18") val over18: Boolean,
    @SerializedName("permalink") val permalink: String,
    @SerializedName("gallery_data") val galleryData: GalleryData?,
    @SerializedName("media_metadata") val mediaMetadata: Map<String, MediaMetadata>?,
    @SerializedName("preview") val preview: Preview?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("thumbnail_height") val thumbnailHeight: Int?,
    @SerializedName("thumbnail_width") val thumbnailWidth: Int?,
    @SerializedName("is_video") val isVideo: Boolean?,
    @SerializedName("sr_detail") val subredditDetails: SubredditDetails,
    val media: Media?
)

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

data class SubredditDetails(
    @SerializedName("community_icon") val communityIcon: String?,
    @SerializedName("icon_img") val iconImg: String?,
)

data class Media(
    @SerializedName("reddit_video") val redditVideo: RedditVideo,
)

data class RedditVideo(
    val width: Int,
    val height: Int,
    @SerializedName("hls_url") val hlsUrl: String,
    val duration: Int,
    @SerializedName("is_gif") val isGif: Boolean,
    @SerializedName("fallback_url") val fallbackUrl: String,
)

data class GalleryData(
    @SerializedName("items") val items: List<GalleryItem>
)

data class MediaMetadata(
    @SerializedName("id") val id: String?,
    @SerializedName("m") val m: String?,
    @SerializedName("p") val p: List<MediaMetadataImage>?,
    @SerializedName("s") val s: MediaMetadataImageSource?,
    @SerializedName("dashUrl") val dashUrl: String?,
    @SerializedName("x") val x: Int?,
    @SerializedName("y") val y: Int?,
    @SerializedName("hlsUrl") val hlsUrl: String?,
    @SerializedName("isGif") val isGif: Boolean?,
)

data class MediaMetadataImage(
    @SerializedName("y") val y: Int,
    @SerializedName("x") val x: Int,
    @SerializedName("u") val u: String,
)

data class MediaMetadataImageSource(
    @SerializedName("y") val y: Int,
    @SerializedName("x") val x: Int,
    @SerializedName("mp4") val mp4: String?,
    @SerializedName("gif") val gif: String?,
    @SerializedName("u") val u: String?,
)

data class ImageSource(
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)

data class GalleryItem(
    @SerializedName("media_id") val mediaId: String,
    @SerializedName("id") val id: Int
)

data class Preview(
    @SerializedName("images") val images: List<PreviewImage>,
    @SerializedName("reddit_video_preview") val redditVideoPreview: RedditVideoPreview?,
    @SerializedName("enabled") val enabled: Boolean?
)

data class PreviewImage(
    @SerializedName("reddit_video_preview") val redditVideoPreview: RedditVideoPreview?,
    @SerializedName("source") val source: ImageSource,
    @SerializedName("resolutions") val resolutions: List<ImageSource>,
    @SerializedName("id") val id: String
)

data class RedditVideoPreview(
    @SerializedName("hls_url") val hlsUrl: String?,
    val width: Int,
    val height: Int,
)


