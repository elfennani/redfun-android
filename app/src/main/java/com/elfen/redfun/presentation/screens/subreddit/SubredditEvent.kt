package com.elfen.redfun.presentation.screens.subreddit

import com.elfen.redfun.domain.model.Sorting

sealed class SubredditEvent {
    data class UpdateSorting(val sorting: Sorting): SubredditEvent()
}