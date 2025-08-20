package com.elfen.redfun.data.remote.models

import com.elfen.redfun.domain.model.Comment
import com.elfen.redfun.domain.model.MediaImage
import com.google.gson.annotations.SerializedName
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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