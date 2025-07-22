package com.elfen.redfun.data

import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.domain.models.Profile
import com.elfen.redfun.ui.utils.Resource
import com.elfen.redfun.ui.utils.resourceOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileService @Inject constructor(private val apiService: AuthAPIService) {
    suspend fun getActiveProfile(): Resource<Profile> {
        return resourceOf{
            val profile = apiService.getUserProfile()

            return@resourceOf Profile(
                id = profile.id,
                username = profile.name,
                fullname = profile.subreddit.title,
                icon = profile.iconImg
            )
        }
    }
}