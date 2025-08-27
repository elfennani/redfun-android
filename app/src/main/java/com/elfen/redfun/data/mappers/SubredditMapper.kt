package com.elfen.redfun.data.mappers

import com.elfen.redfun.data.remote.models.RemoteSubreddit
import com.elfen.redfun.domain.model.Subreddit

fun RemoteSubreddit.asDomainModel() = Subreddit(
    id = id,
    name = displayName,
    title = title,
    description = description,
    iconUrl = iconImg?.ifEmpty { null } ?: communityIcon?.ifEmpty { null },
    bannerUrl = bannerImg?.ifEmpty { null },
    subscribers = subscribers,
    isNSFW = nsfw
)