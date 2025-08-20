package com.elfen.redfun.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.redfun.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingRepository.settingsFlow.collect { settings ->
                _state.value = _state.value.copy(
                    settings = settings,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    fun updateMaxWifiResolution(maxWifiResolution: Int) {
        viewModelScope.launch {
            try {
                settingRepository.updateMaxWifiResolution(maxWifiResolution)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to update WiFi resolution: ${e.message}"
                )
            }
        }
    }

    fun updateMaxMobileResolution(maxMobileResolution: Int) {
        viewModelScope.launch {
            try {
                settingRepository.updateMaxMobileResolution(maxMobileResolution)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to update mobile resolution: ${e.message}"
                )
            }
        }
    }
}