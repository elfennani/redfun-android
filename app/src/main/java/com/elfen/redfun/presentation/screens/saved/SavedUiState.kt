package com.elfen.redfun.presentation.screens.saved

import androidx.paging.PagingData
import com.elfen.redfun.data.SettingsRepositoryImpl
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class SavedUiState(
    val posts: Flow<PagingData<Post>> = flow { PagingData.empty<Post>() },
    val isLoading: Boolean = false,
    val error: String? = null,
    val displayMode: DisplayMode = SettingsRepositoryImpl.DefaultDisplayMode,
    val isNavBarShown: Boolean = true
)
