package com.elfen.redfun.ui.screens.auth

sealed class AuthState {
    object Loading : AuthState()
    data class Error(val message: String, val retry: () -> Unit) : AuthState()
    object Success : AuthState()
}