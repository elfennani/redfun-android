package com.elfen.redfun.presentation.screens.feed

import androidx.paging.PagingData
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.domain.model.Sorting
import kotlinx.coroutines.flow.Flow

data class FeedState(
    val posts: Flow<PagingData<Post>>? = null,
    val isLoading: Boolean = false,
    val isFetchingNextPage: Boolean = false,
    val error: String? = null,
    val isFetchingNextPageError: Boolean = false,
    val isError: Boolean = false,
    val sorting: Sorting? = null,
    val displayMode: DisplayMode = DisplayMode.MASONRY,
    // ---
    val refresh: () -> Unit = {},
    val onSortingChanged: (Sorting) -> Unit = {},
    val onDisplayModeChanged: (DisplayMode) -> Unit = {}
)
