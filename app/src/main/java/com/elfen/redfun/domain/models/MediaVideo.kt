package com.elfen.redfun.domain.models

data class MediaVideo(
    val source: String,
    val width: Int,
    val height: Int,
    val duration: Int?,
    val isGif: Boolean,
    val fallback: String?
)
