package com.elfen.redfun.domain.model

data class MediaImage(
    val id: String,
    val source: String,
    val width: Int,
    val height: Int,
    val animated: Boolean,
)