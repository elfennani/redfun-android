package com.elfen.redfun.data.remote.models

import com.google.gson.annotations.SerializedName

/*
id: z.string(),
  icon_img: z.string().transform(stripEntities).optional(),
  name: z.string(),
  subreddit: z.object({
    title: z.string().transform(stripEntities).optional(),
  }),
 */
data class RemoteProfile(
    val id: String,
    val name: String,
    val subreddit: Subreddit,
    @SerializedName("icon_img") val iconImg: String?,
)
