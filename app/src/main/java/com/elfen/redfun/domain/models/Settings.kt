package com.elfen.redfun.domain.models

data class Settings(
    val maxWifiResolution: Int = 1080,
    val maxMobileResolution: Int = 720,
){
    companion object {
        val Default = Settings()
    }
}
