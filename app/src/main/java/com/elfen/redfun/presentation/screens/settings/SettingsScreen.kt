package com.elfen.redfun.presentation.screens.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elfen.redfun.presentation.screens.feed.plus
import com.elfen.redfun.presentation.screens.settings.components.DropdownSetting


@Composable
fun SettingsScreen(navController: NavHostController) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    val settings by viewModel.state.collectAsState()

    SettingsScreen(
        state = settings,
        onUpdateMaxWifiResolution = { resolution ->
            viewModel.updateMaxWifiResolution(resolution)
        },
        onUpdateMaxMobileResolution = { resolution ->
            viewModel.updateMaxMobileResolution(resolution)
        },
        onBack = {
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    onBack: () -> Unit = {},
    state: SettingsUiState,
    onUpdateMaxWifiResolution: (Int) -> Unit = {},
    onUpdateMaxMobileResolution: (Int) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { scaffoldPadding ->
        if (!state.isLoading && state.settings != null)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = scaffoldPadding + PaddingValues(horizontal = 16.dp) + PaddingValues(
                    bottom = 16.dp
                )
            ) {
                item {
                    DropdownSetting(
                        title = "Max WiFi video resolution",
                        options = listOf(
                            144, 240, 360, 480, 720, 1080, 1440, 2160
                        ),
                        selectedOption = state.settings.maxWifiResolution,
                        onOptionSelected = { onUpdateMaxWifiResolution(it) },
                        optionLabel = { resolution ->
                            "${resolution}p"
                        }
                    )
                }
                item {
                    DropdownSetting(
                        title = "Max Mobile video resolution",
                        options = listOf(
                            144, 240, 360, 480, 720, 1080, 1440, 2160
                        ),
                        selectedOption = state.settings.maxMobileResolution,
                        onOptionSelected = { onUpdateMaxMobileResolution(it) },
                        optionLabel = { resolution ->
                            "${resolution}p"
                        }
                    )
                }
            }
    }
}