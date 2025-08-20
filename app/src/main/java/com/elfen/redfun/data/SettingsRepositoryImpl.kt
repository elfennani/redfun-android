package com.elfen.redfun.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Settings
import com.elfen.redfun.domain.repository.SettingsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Composable
fun rememberSettings(): State<Settings?> {
    val context = LocalContext.current
    return SettingsRepositoryImpl.getSettingsFlow(context.dataStore).collectAsState(null)
}

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : SettingsRepository {

    override val settingsFlow: Flow<Settings>
        get() = getSettingsFlow(dataStore)
    override val navBarShown: Flow<Boolean>
        get() = dataStore.data.map { prefs ->
            prefs[NavBarShownKey] ?: true // Default to true if not set
        }

    override suspend fun updateNavBarShown(isShown: Boolean) {
        dataStore.edit { settings ->
            settings[NavBarShownKey] = isShown
        }
    }

    override val globalDisplayModeFlow: Flow<DisplayMode>
        get() = dataStore.data.map { prefs ->
            val mode = prefs[GlobalDisplayModeKey] ?: DefaultDisplayMode.name
            DisplayMode.valueOf(mode)
        }

    override suspend fun updateGlobalDisplayMode(displayMode: DisplayMode) {
        dataStore.edit { settings ->
            settings[GlobalDisplayModeKey] = displayMode.name
        }
    }

    override suspend fun updateDisplayModeForSubreddit(
        subreddit: String,
        displayMode: DisplayMode
    ) {
        dataStore.edit { prefs ->
            val subModeJson = prefs[SubredditDisplayModeKey] ?: "{}"
            val typeToken = object : TypeToken<MutableMap<String, String>>() {}.type
            val subModeMap: MutableMap<String, String> = gson.fromJson(subModeJson, typeToken)

            subModeMap[subreddit] = displayMode.name
            prefs[SubredditDisplayModeKey] = gson.toJson(subModeMap, typeToken)
        }
    }

    override fun getDisplayModeForSubreddit(subreddit: String): Flow<DisplayMode> {
        return dataStore.data.map { prefs ->
            val globalMode = prefs[GlobalDisplayModeKey] ?: DefaultDisplayMode.name
            val subModeJson = prefs[SubredditDisplayModeKey] ?: "{}"
            val typeToken = object : TypeToken<MutableMap<String, String>>() {}.type
            val subModeMap: MutableMap<String, String> = gson.fromJson(subModeJson, typeToken)

            val mode = subModeMap[subreddit] ?: globalMode
            DisplayMode.valueOf(mode)
        }
    }

    override suspend fun updateMaxWifiResolution(maxWifiResolution: Int) {
        dataStore.edit { settings ->
            settings[MaxWifiResolutionKey] = maxWifiResolution
        }
    }


    override suspend fun updateMaxMobileResolution(maxMobileResolution: Int) {
        dataStore.edit { settings ->
            settings[MaxMobileResolutionKey] = maxMobileResolution
        }
    }

    companion object {
        val MaxWifiResolutionKey = intPreferencesKey("wifi_max_resolution")
        val MaxMobileResolutionKey = intPreferencesKey("mobile_max_resolution")
        val GlobalDisplayModeKey = stringPreferencesKey("global_display_mode")
        val SubredditDisplayModeKey = stringPreferencesKey("subreddit_display_mode")
        val NavBarShownKey = booleanPreferencesKey("nav_bar_shown")

        val DefaultDisplayMode = DisplayMode.LIST

        fun getSettingsFlow(dataStore: DataStore<Preferences>): Flow<Settings> {
            return dataStore.data.map { prefs ->
                Settings(
                    maxWifiResolution = prefs[MaxWifiResolutionKey]
                        ?: Settings.Default.maxWifiResolution,
                    maxMobileResolution = prefs[MaxMobileResolutionKey]
                        ?: Settings.Default.maxMobileResolution
                )
            }
        }
    }
}