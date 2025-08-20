package com.elfen.redfun.domain.repository

import com.elfen.redfun.domain.model.Profile
import com.elfen.redfun.presentation.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun fetchActiveProfile(): Resource<Unit>
    fun getProfileByUserID(userId: String): Flow<Profile?>
}