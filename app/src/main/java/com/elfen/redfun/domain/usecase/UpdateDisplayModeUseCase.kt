package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateDisplayModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(displayMode: DisplayMode, subreddit: String? = null) =
        if (subreddit == null) {
            settingsRepository.updateGlobalDisplayMode(displayMode)
        } else {
            settingsRepository.updateDisplayModeForSubreddit(subreddit, displayMode)
        }
}