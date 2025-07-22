package com.elfen.redfun.data.remote.models

import com.elfen.redfun.data.local.models.ProfileEntity
import com.google.gson.annotations.SerializedName

data class RemoteProfile(
    val id: String,
    val name: String,
    val subreddit: Subreddit,
    @SerializedName("icon_img") val iconImg: String?,
    @SerializedName("comment_karma") val commentKarma: Int,
    @SerializedName("link_karma") val linkKarma: Int,
    @SerializedName("total_karma") val totalKarma: Int,
    @SerializedName("is_mod") val isMod: Boolean,
)

fun RemoteProfile.toEntity() = ProfileEntity(
    id = id,
    username = name,
    fullName = subreddit.title,
    icon = iconImg?.ifEmpty { null } ?: subreddit.iconImg?.ifEmpty { null } ?: subreddit.communityIcon?.ifEmpty { null },
    banner = subreddit.bannerImg?.ifEmpty { null },
    commentKarma = commentKarma,
    linkKarma = linkKarma,
    totalKarma = totalKarma,
    isMod = isMod
)