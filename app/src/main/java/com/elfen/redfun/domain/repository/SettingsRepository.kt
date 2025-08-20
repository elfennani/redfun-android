package com.elfen.redfun.domain.repository

import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<Settings>
    val globalDisplayModeFlow: Flow<DisplayMode>
    val navBarShown: Flow<Boolean>

    fun getDisplayModeForSubreddit(subreddit: String): Flow<DisplayMode>
    suspend fun updateDisplayModeForSubreddit(subreddit: String, displayMode: DisplayMode)
    suspend fun updateGlobalDisplayMode(displayMode: DisplayMode)
    suspend fun updateNavBarShown(isShown: Boolean)

    suspend fun updateMaxWifiResolution(maxWifiResolution: Int)
    suspend fun updateMaxMobileResolution(maxMobileResolution: Int)
}