package com.elfen.redfun.presentation.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.usecase.GetDisplayModeUseCase
import com.elfen.redfun.domain.usecase.GetFeedPagingUseCase
import com.elfen.redfun.domain.usecase.GetNavBarShownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    getFeedPaging: GetFeedPagingUseCase,
    getDisplayMode: GetDisplayModeUseCase,
    getNavBarShown: GetNavBarShownUseCase
) : ViewModel() {
    val state = combine(getDisplayMode(), getNavBarShown()){ displayMode, navBarShown ->
        SavedUiState(
            posts = getFeedPaging(Feed.SavedPosts).cachedIn(viewModelScope),
            isLoading = false,
            displayMode = displayMode,
            isNavBarShown = navBarShown
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, SavedUiState(isLoading = true))
}