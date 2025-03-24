package com.elfen.redfun.data.remote.models

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("access_token") val token: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String
)
