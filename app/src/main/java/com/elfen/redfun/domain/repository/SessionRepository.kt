package com.elfen.redfun.domain.repository

import com.elfen.redfun.data.local.models.SessionEntity
import com.elfen.redfun.presentation.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun sessions(): Flow<List<SessionEntity>>

    suspend fun authenticate(code: String): Resource<String>
    suspend fun changeSession(sessionId: String)
    suspend fun resetCache()
    suspend fun getCurrentSession(): SessionEntity?
    suspend fun refreshSession(sessionId: String): SessionEntity
}