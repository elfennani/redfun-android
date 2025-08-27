package com.elfen.redfun.presentation.screens.search

import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Sorting

sealed class SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent()
    data class Search(val query: String) : SearchEvent()
    data class ChangeSorting(val sorting: Sorting) : SearchEvent()
    data class ChangeDisplayMode(val displayMode: DisplayMode) : SearchEvent()
    data class SelectSubreddit(val subreddit: String?) : SearchEvent()
    object ClearQuery : SearchEvent()
}