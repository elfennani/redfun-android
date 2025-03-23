package com.elfen.redfun.domain.models

data class MediaImage(
    val id: String? = null,
    val source: String,
    val width: Int,
    val height: Int,
    val animated: Boolean,
)