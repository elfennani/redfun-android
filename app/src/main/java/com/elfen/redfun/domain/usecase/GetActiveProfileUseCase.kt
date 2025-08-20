package com.elfen.redfun.domain.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elfen.redfun.domain.model.Profile
import com.elfen.redfun.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetActiveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val dataStore: DataStore<Preferences>
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Profile?> {
        val sessionIdFlow = dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey("session_id")]!!
            }

        return sessionIdFlow.flatMapMerge {
            profileRepository.getProfileByUserID(userId = it)
        }
    }
}