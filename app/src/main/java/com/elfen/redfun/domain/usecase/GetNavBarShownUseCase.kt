package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.repository.SettingsRepository
import javax.inject.Inject

class GetNavBarShownUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() = settingsRepository.navBarShown
}