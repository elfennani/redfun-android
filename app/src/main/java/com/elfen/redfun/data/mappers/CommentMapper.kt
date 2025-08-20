package com.elfen.redfun.data.mappers

import com.elfen.redfun.data.remote.models.RemoteComment
import com.elfen.redfun.domain.model.Comment
import com.elfen.redfun.domain.model.MediaImage
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun RemoteComment.asDomainModel(): Comment {
    return if (this is RemoteComment.Body) {
        val media = mediaMetadata?.values?.firstOrNull().let {
            if (it?.hlsUrl !== null) return@let null
            else if (it != null) return@let MediaImage(
                source = it.s!!.u ?: it.s.gif ?: return@let null,
                width = it.s.x,
                height = it.s.y,
                id = it.id!!,
                animated = it.s.gif != null,
            )

            return@let null
        }
        Comment.Body(
            id = id,
            body = body,
            score = score,
            depth = depth,
            images = if(media !== null) listOf(media) else null,
            author = author,
            created = Instant.fromEpochSeconds(created)
        )
    } else if (this is RemoteComment.More)
        Comment.More(id, depth, count)
    else
        throw Error("Unknown comment type")
}