package com.elfen.redfun.domain.repository

import com.elfen.redfun.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<Settings>
    suspend fun updateMaxWifiResolution(maxWifiResolution: Int)
    suspend fun updateMaxMobileResolution(maxMobileResolution: Int)
}