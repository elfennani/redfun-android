package com.elfen.redfun.domain.model

data class Subreddit(
    val id: String,
    val name: String,
    val title: String?,
    val description: String?,
    val iconUrl: String?,
    val bannerUrl: String?,
    val subscribers: Int,
    val isNSFW: Boolean
)
