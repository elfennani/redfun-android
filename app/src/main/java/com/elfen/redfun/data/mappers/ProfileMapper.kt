package com.elfen.redfun.data.mappers

import com.elfen.redfun.data.local.models.ProfileEntity
import com.elfen.redfun.data.remote.models.RemoteProfile
import com.elfen.redfun.domain.model.Profile
import kotlin.text.ifEmpty

fun ProfileEntity.asDomainModel() = Profile(
    id = id,
    username = username,
    fullName = fullName,
    icon = icon,
    banner = banner,
    commentKarma = commentKarma,
    linkKarma = linkKarma,
    totalKarma = totalKarma,
    isMod = isMod
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