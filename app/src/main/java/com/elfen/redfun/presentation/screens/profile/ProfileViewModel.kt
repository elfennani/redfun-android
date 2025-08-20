package com.elfen.redfun.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.redfun.domain.usecase.FetchActiveProfileUseCase
import com.elfen.redfun.domain.usecase.GetActiveProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getActiveProfile: GetActiveProfileUseCase,
    private val fetchActiveProfile: FetchActiveProfileUseCase
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val profile = getActiveProfile().stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            fetchActiveProfile()
        }
    }
}