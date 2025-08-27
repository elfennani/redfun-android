package com.elfen.redfun.presentation.screens.flair

import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Sorting

sealed class FlairEvent {
    data class ChangeSorting(val sorting: Sorting): FlairEvent()
    data class ChangeDisplayMode(val displayMode: DisplayMode): FlairEvent()
    data object ToggleNavBar: FlairEvent()
}