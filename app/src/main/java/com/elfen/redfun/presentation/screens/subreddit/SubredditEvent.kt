package com.elfen.redfun.presentation.screens.subreddit

import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Sorting

sealed class SubredditEvent {
    data class ChangeSorting(val sorting: Sorting): SubredditEvent()
    data class ChangeDisplayMode(val displayMode: DisplayMode): SubredditEvent()
    data object ToggleNavBar: SubredditEvent()
}