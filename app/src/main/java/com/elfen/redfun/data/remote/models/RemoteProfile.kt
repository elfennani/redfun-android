package com.elfen.redfun.data.remote.models

import com.google.gson.annotations.SerializedName

data class RemoteProfile(
    val id: String,
    val name: String,
    val subreddit: RemoteSubreddit?,
    @SerializedName("is_suspended") val isSuspended: Boolean,
    @SerializedName("is_blocked") val isBlocked: Boolean,
    @SerializedName("icon_img") val iconImg: String?,
    @SerializedName("comment_karma") val commentKarma: Int,
    @SerializedName("link_karma") val linkKarma: Int,
    @SerializedName("total_karma") val totalKarma: Int,
    @SerializedName("is_mod") val isMod: Boolean,
)