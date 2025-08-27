package com.elfen.redfun.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_media")
data class PostMediaEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val source: String,
    val width: Int,
    val height: Int,

    val isVideo: Boolean?,
    val duration: Int?,
    val isGif: Boolean? ,
    val fallback: String?,

    @ColumnInfo(defaultValue = "0")
    val index: Int = 0
)
