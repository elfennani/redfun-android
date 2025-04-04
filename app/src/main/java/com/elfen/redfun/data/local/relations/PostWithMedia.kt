package com.elfen.redfun.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.elfen.redfun.data.local.models.PostEntity
import com.elfen.redfun.data.local.models.PostMediaEntity
import com.elfen.redfun.domain.models.MediaImage
import com.elfen.redfun.domain.models.Post
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class PostWithMedia(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val media: List<PostMediaEntity>
)

@OptIn(ExperimentalTime::class)
fun PostWithMedia.asAppModel() = Post(
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
    images = media.map { MediaImage(
        id = it.id,
        source = it.source,
        width = it.width,
        height = it.height,
        animated = true
    ) }
)