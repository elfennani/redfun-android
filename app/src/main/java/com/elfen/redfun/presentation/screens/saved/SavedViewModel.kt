package com.elfen.redfun.presentation.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.usecase.GetDisplayModeUseCase
import com.elfen.redfun.domain.usecase.GetFeedPagingUseCase
import com.elfen.redfun.domain.usecase.GetNavBarShownUseCase
import com.elfen.redfun.domain.usecase.UpdateDisplayModeUseCase
import com.elfen.redfun.domain.usecase.UpdateNavBarShownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    getFeedPaging: GetFeedPagingUseCase,
    getDisplayMode: GetDisplayModeUseCase,
    getNavBarShown: GetNavBarShownUseCase,
    private val updateNavBarShown: UpdateNavBarShownUseCase,
    private val updateDisplayMode: UpdateDisplayModeUseCase
) : ViewModel() {
    val state = combine(getDisplayMode(), getNavBarShown()){ displayMode, navBarShown ->
        SavedUiState(
            posts = getFeedPaging(Feed.SavedPosts).cachedIn(viewModelScope),
            isLoading = false,
            displayMode = displayMode,
            isNavBarShown = navBarShown
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, SavedUiState(isLoading = true))

    fun onEvent(event: SavedEvent) {
        when(event) {
            is SavedEvent.ToggleNavBar -> {
                viewModelScope.launch {
                    updateNavBarShown(!state.value.isNavBarShown)
                }
            }
            is SavedEvent.ChangeDisplayMode -> {
                viewModelScope.launch {
                    updateDisplayMode(event.displayMode)
                }
            }
        }
    }
}