package com.elfen.redfun.domain.models

data class MediaImage(
    val id: String,
    val source: String,
    val width: Int,
    val height: Int,
    val animated: Boolean,
)