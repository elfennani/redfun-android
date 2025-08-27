package com.elfen.redfun.data.remote.models

import com.google.gson.annotations.SerializedName

data class RemoteSubreddit(
    val id: String,
    @SerializedName("display_name") val displayName: String,
    val title: String?,
    val subscribers: Int,
    @SerializedName("public_description") val description: String?,
    @SerializedName("icon_img") val iconImg: String?,
    @SerializedName("banner_img") val bannerImg: String? = null,
    @SerializedName("community_icon") val communityIcon: String? = null,
    @SerializedName("over18") val nsfw: Boolean
)
