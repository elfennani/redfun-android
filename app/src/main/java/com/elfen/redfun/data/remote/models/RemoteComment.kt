package com.elfen.redfun.data.remote.models

import com.elfen.redfun.domain.models.Comment
import com.elfen.redfun.domain.models.MediaImage
import com.google.gson.annotations.SerializedName
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/*
export const CommentSchema = z
  .object({
    id: z.string(),
    author: z.string(),
    created: EpochDate,
    body: z.string().transform(stripEntities),
    score: z.number(),
    depth: z.number(),
    body_html: z.string().transform(stripEntities),
    media_metadata: MediaMetadataSchema.optional(),
  })
  .transform((data) => ({ ...data, type: "comment" as const }))
  .or(
    z
      .object({
        count: z.number(),
        id: z.string(),
        parent_id: z.string(),
        depth: z.number(),
        children: z.array(z.string()),
      })
      .transform((data) => ({ ...data, type: "more" as const }))
  );

 */

sealed class RemoteComment {
    data class Body(
        val id: String,
        val body: String,
        val author: String,
        val created: Long,
        val score: Int,
        val depth: Int,
        @SerializedName("media_metadata") val mediaMetadata: Map<String, MediaMetadata>?,
    ) : RemoteComment()

    data class More(
        val id: String,
        val count: Int,
        @SerializedName("parent_id") val parentId: String,
        val depth: Int,
        val children: List<String>,
    ) : RemoteComment()
}

@OptIn(ExperimentalTime::class)
fun RemoteComment.asDomainModel(): Comment {
    return if (this is RemoteComment.Body) {
        val media = mediaMetadata?.values?.firstOrNull().let {
            if (it?.hlsUrl !== null) return@let null
            else if (it != null) return@let MediaImage(
                source = it.s!!.u ?: it.s.gif ?: return@let null,
                width = it.s.x,
                height = it.s.y,
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