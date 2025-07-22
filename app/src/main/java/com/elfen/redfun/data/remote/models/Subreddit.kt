package com.elfen.redfun.data.remote.models

import com.google.gson.annotations.SerializedName

data class Subreddit(
    val title: String?,
    val description: String? = null,
    @SerializedName("icon_img") val iconImg: String?,
    @SerializedName("banner_img") val bannerImg: String? = null,
    @SerializedName("community_icon") val communityIcon: String? = null,
)
