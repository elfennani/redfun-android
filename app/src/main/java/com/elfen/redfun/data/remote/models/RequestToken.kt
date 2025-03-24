package com.elfen.redfun.data.remote.models

import com.google.gson.annotations.SerializedName

data class RequestToken(
    @SerializedName("grant_type") val grantType: String,
    val code: String,
    @SerializedName("redirect_uri") val redirectUri: String
)
