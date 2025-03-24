package com.elfen.redfun.data

import com.elfen.redfun.BuildConfig
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elfen.redfun.data.local.dao.SessionDao
import com.elfen.redfun.data.local.models.SessionEntity
import com.elfen.redfun.data.remote.PublicAPIService
import com.elfen.redfun.ui.utils.Resource
import com.elfen.redfun.ui.utils.resourceOf
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Singleton
class SessionRepo @Inject constructor(private val publicApiService: PublicAPIService, private val sessionDao: SessionDao, private val dataStore: DataStore<Preferences>) {
    @OptIn(ExperimentalTime::class)
    suspend fun authenticate(code: String): Resource<String> {
        val clientId = Base64.encodeToString((BuildConfig.clientId + ":").toByteArray(), Base64.NO_WRAP)
        val redirectUri = BuildConfig.redirectUri

        return resourceOf {
            val response = publicApiService.getAccessToken(
                grantType= "authorization_code",
                code=code,
                redirectUri = redirectUri,
                auth = "Basic $clientId"
            )
            val profile = publicApiService.getUserProfile("Bearer ${response.token}")
            
            sessionDao.upsertSession(SessionEntity(
                userId = profile.id,
                token = response.token,
                refreshToken = response.refreshToken,
                expiresAt = Clock.System.now().epochSeconds + response.expiresIn,
                username = profile.name,
                displayName = profile.subreddit.title,
                avatarUrl = profile.iconImg
            ))

            dataStore.edit {
                it[stringPreferencesKey("session_id")] = profile.id
            }

            return@resourceOf profile.id
        }
    }
}