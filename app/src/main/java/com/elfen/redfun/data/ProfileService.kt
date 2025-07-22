package com.elfen.redfun.data

import com.elfen.redfun.data.local.dao.ProfileDao
import com.elfen.redfun.data.local.models.toAppModel
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.data.remote.models.toEntity
import com.elfen.redfun.domain.models.Profile
import com.elfen.redfun.ui.utils.Resource
import com.elfen.redfun.ui.utils.resourceOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileService @Inject constructor(private val apiService: AuthAPIService, private val profileDao: ProfileDao) {
    suspend fun fetchActiveProfile(): Resource<Unit> {
        return resourceOf{
            val profile = apiService.getUserProfile()
            profileDao.upsertProfile(profile.toEntity())
        }
    }

    fun getProfileByUserID(userId: String) = profileDao.getProfileByUserID(userId).map { it?.toAppModel() }
    fun getProfileByUsername(username: String) = profileDao.getProfileByUsername(username).map { it?.toAppModel() }
}