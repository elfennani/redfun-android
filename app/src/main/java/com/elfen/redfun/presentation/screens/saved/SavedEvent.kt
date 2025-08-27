package com.elfen.redfun.presentation.screens.saved

import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.presentation.screens.feed.FeedEvent

sealed class SavedEvent {
    data object ToggleNavBar : SavedEvent()
    data class ChangeDisplayMode(val displayMode: DisplayMode): SavedEvent()
}