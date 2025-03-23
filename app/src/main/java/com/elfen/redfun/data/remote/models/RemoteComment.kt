package com.elfen.redfun.data.remote.models

import com.elfen.redfun.domain.models.Comment
import com.google.gson.annotations.SerializedName

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

fun RemoteComment.asDomainModel(): Comment {
    return if (this is RemoteComment.Body)
        Comment.Body(id, body)
    else if (this is RemoteComment.More)
        Comment.More(id)
    else
        throw Error("Unknown comment type")
}