package com.elfen.redfun.presentation.screens.flair

import androidx.compose.foundation.pager.PagerState
import androidx.paging.PagingData
import com.elfen.redfun.data.SettingsRepositoryImpl
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class FlairUiState(
    val flair: String = "",
    val posts: Flow<PagingData<Post>> = flowOf(PagingData.empty()),
    val sorting: Sorting = FeedRepository.DEFAULT_SORTING,
    val displayMode: DisplayMode = SettingsRepositoryImpl.DefaultDisplayMode,
    val navBarShown: Boolean = true,
)
