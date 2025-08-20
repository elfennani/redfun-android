package com.elfen.redfun.presentation.screens.settings

import com.elfen.redfun.domain.model.Settings

data class SettingsUiState(
    val settings: Settings? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)