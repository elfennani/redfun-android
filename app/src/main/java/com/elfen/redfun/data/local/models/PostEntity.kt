package com.elfen.redfun.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
data class PostEntity(
    @PrimaryKey
    val id: String,
    val body: String?,
    val subreddit: String,
    val subredditIcon: String? = null,
    val score: Int,
    val numComments: Int,
    val author: String,
    val created: Long,
    val thumbnail: String?,
    val url: String,
    val title: String,
    val nsfw: Boolean,
    val link: String?,
    @ColumnInfo(defaultValue = "NULL")
    val flair: String? = null,
)
