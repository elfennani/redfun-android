package com.elfen.redfun.ui.screens.home

import androidx.paging.PagingData
import com.elfen.redfun.domain.models.Post
import com.elfen.redfun.domain.models.Sorting
import kotlinx.coroutines.flow.Flow

data class HomeState(
    val posts: Flow<PagingData<Post>>? = null,
    val isLoading: Boolean = false,
    val isFetchingNextPage: Boolean = false,
    val error: String? = null,
    val isFetchingNextPageError: Boolean = false,
    val isError: Boolean = false,
    val sorting: Sorting? = null,
    // ---
    val refresh: () -> Unit = {},
    val onSortingChanged: (Sorting) -> Unit = {}
)
