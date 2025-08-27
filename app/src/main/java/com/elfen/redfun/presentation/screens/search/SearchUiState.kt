package com.elfen.redfun.presentation.screens.search

import androidx.paging.PagingData
import com.elfen.redfun.data.SettingsRepositoryImpl
import com.elfen.redfun.domain.model.AutoCompleteResult
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.domain.model.Sorting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class SearchUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val searchedQuery: String = "",
    val autoCompleteResult: AutoCompleteResult? = null,
    val posts: Flow<PagingData<Post>> = flow { PagingData.empty<Post>() },
    val sorting: Sorting? = null,
    val displayMode: DisplayMode = SettingsRepositoryImpl.DefaultDisplayMode,
)
