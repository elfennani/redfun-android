package com.elfen.redfun.domain.model

data class Profile(
    val id: String,
    val username: String,
    val fullName: String?,
    val icon: String?,
    val banner: String?,
    val commentKarma: Int,
    val linkKarma: Int,
    val totalKarma: Int,
    val isMod: Boolean,
)
