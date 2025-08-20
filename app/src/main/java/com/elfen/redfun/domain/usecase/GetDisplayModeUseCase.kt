package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.repository.SettingsRepository
import javax.inject.Inject

class GetDisplayModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(subreddit: String? = null) = if (subreddit == null) {
        settingsRepository.globalDisplayModeFlow
    } else {
        settingsRepository.getDisplayModeForSubreddit(subreddit)
    }
}