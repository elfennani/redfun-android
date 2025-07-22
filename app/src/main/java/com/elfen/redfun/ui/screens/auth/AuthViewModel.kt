package com.elfen.redfun.ui.screens.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.redfun.data.SessionRepo
import com.elfen.redfun.ui.screens.auth.AuthState.*
import com.elfen.redfun.ui.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepo: SessionRepo
) : ViewModel() {
    val code = savedStateHandle.toRoute<AuthRoute>().code
    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state = _state.asStateFlow()

    init {
        if (code != null)
            viewModelScope.launch {
                when (val res = sessionRepo.authenticate(code)) {
                    is Resource.Error -> _state.value = Error(res.message!!, retry = {})
                    is Resource.Success -> _state.value = Success
                }
            }
    }
}