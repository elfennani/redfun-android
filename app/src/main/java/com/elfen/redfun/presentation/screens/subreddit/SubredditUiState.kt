package com.elfen.redfun.presentation.screens.subreddit

import androidx.paging.PagingData
import com.elfen.redfun.data.SettingsRepositoryImpl
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow

data class SubredditUiState(
    val posts: Flow<PagingData<Post>>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val sorting: Sorting = FeedRepository.DEFAULT_SORTING,
    val displayMode: DisplayMode = SettingsRepositoryImpl.DefaultDisplayMode,
    val isNavBarShown: Boolean = true,
)
