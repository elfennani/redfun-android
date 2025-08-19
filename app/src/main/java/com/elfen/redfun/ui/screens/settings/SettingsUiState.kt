package com.elfen.redfun.ui.screens.settings

import com.elfen.redfun.domain.models.Settings
import kotlinx.coroutines.flow.StateFlow

data class SettingsUiState(
    val settings: Settings? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)