package com.elfen.redfun.presentation.screens.feed

import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Sorting

sealed class FeedEvent {
    object Refetch: FeedEvent()
    data class ChangeSorting(val sorting: Sorting): FeedEvent()
    data class ChangeDisplayMode(val displayMode: DisplayMode): FeedEvent()
    data object ToggleNavBar: FeedEvent()
}