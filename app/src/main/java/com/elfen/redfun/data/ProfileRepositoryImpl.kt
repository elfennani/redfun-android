package com.elfen.redfun.data

import com.elfen.redfun.data.local.dao.ProfileDao
import com.elfen.redfun.data.mappers.asDomainModel
import com.elfen.redfun.data.mappers.toEntity
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.domain.repository.ProfileRepository
import com.elfen.redfun.presentation.utils.Resource
import com.elfen.redfun.presentation.utils.resourceOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val apiService: AuthAPIService,
    private val profileDao: ProfileDao
) : ProfileRepository {
    override suspend fun fetchActiveProfile(): Resource<Unit> {
        return resourceOf {
            val profile = apiService.getUserProfile()
            profileDao.upsertProfile(profile.toEntity())
        }
    }

    override fun getProfileByUserID(userId: String) =
        profileDao.getProfileByUserID(userId).map { it?.asDomainModel() }

}