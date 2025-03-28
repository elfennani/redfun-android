package com.elfen.redfun.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.elfen.redfun.data.FeedService
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.ui.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val feedService: FeedService) : ViewModel() {
    val state = feedService.getSortingFlow().map {
        val sorting = it ?: Sorting.Best
        HomeState(
            isLoading = false,
            posts = feedService.getFeedPaging(sorting).cachedIn(viewModelScope),
            sorting = sorting,
            onSortingChanged = ::updateSorting
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState(isLoading = true))

    private fun updateSorting(sorting: Sorting) {
        viewModelScope.launch {
            feedService.setSorting(sorting)
        }
    }
}