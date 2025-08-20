package com.elfen.redfun.domain.model

data class MediaVideo(
    val source: String,
    val width: Int,
    val height: Int,
    val duration: Int?,
    val isGif: Boolean,
    val fallback: String?
)
