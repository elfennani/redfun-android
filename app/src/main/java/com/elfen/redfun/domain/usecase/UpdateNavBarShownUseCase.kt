package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateNavBarShownUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(isShown: Boolean) {
        settingsRepository.updateNavBarShown(isShown)
    }
}