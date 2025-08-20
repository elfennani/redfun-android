package com.elfen.redfun.presentation.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.usecase.GetFeedPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    getFeedPaging: GetFeedPagingUseCase
) : ViewModel() {
    val posts = getFeedPaging(Feed.SavedPosts).cachedIn(viewModelScope)
}