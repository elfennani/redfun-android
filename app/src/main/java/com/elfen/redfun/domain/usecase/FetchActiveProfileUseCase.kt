package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.repository.ProfileRepository
import com.elfen.redfun.presentation.utils.Resource
import javax.inject.Inject

class FetchActiveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Resource<Unit>{
        return profileRepository.fetchActiveProfile()
    }
}