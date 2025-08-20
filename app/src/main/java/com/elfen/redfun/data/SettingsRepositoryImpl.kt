package com.elfen.redfun.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.model.Settings
import com.elfen.redfun.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val maxWifiResolutionKey = intPreferencesKey("wifi_max_resolution")
private val maxMobileResolutionKey = intPreferencesKey("mobile_max_resolution")

private fun getSettingsFlow(context: Context): Flow<Settings> {
    return context.dataStore.data.map { prefs ->
        Settings(
            maxWifiResolution = prefs[maxWifiResolutionKey]
                ?: Settings.Default.maxWifiResolution,
            maxMobileResolution = prefs[maxMobileResolutionKey]
                ?: Settings.Default.maxMobileResolution
        )
    }
}

@Composable
fun rememberSettings(): State<Settings?> {
    val context = LocalContext.current
    return getSettingsFlow(context).collectAsState(null)
}

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override val settingsFlow: Flow<Settings>
        get() = getSettingsFlow(context)

    override suspend fun updateMaxWifiResolution(maxWifiResolution: Int) {
        context.dataStore.edit { settings ->
            settings[maxWifiResolutionKey] = maxWifiResolution
        }
    }

    override suspend fun updateMaxMobileResolution(maxMobileResolution: Int) {
        context.dataStore.edit { settings ->
            settings[maxMobileResolutionKey] = maxMobileResolution
        }
    }
}