package com.elfen.redfun.presentation.screens.saved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.presentation.components.PostList
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.elfen.redfun.presentation.components.ui.AppTopBar
import com.elfen.redfun.presentation.screens.flair.FlairRoute
import com.elfen.redfun.presentation.utils.LocalScaffoldPadding
import com.elfen.redfun.presentation.utils.plus

@Composable
fun SavedScreen(
    navController: NavController,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    SavedScreen(
        state = state,
        navController = navController,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    state: SavedUiState,
    navController: NavController,
    onEvent: (SavedEvent) -> Unit = { }
) {
    val posts = state.posts.collectAsLazyPagingItems()
    val outerPadding = LocalScaffoldPadding.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = {
                    Text("Saved Posts")
                },
                containerColor =
                    if (state.displayMode == DisplayMode.SCROLLER)
                        Color.Black
                    else
                        MaterialTheme.colorScheme.background,
                contentColor =
                    if (state.displayMode == DisplayMode.SCROLLER)
                        Color.White
                    else
                        MaterialTheme.colorScheme.onBackground
            )
        },
    ) {
        PostList(
            posts = posts,
            contentPadding = it + outerPadding,
            navController = navController,
            displayMode = state.displayMode,
            navBarShown = state.isNavBarShown,
            onNavBarShownChange = { onEvent(SavedEvent.ToggleNavBar) },
            onSelectDisplayMode = { mode -> onEvent(SavedEvent.ChangeDisplayMode(mode)) },
            navigateFlair = { subreddit, flair ->
                navController.navigate(
                    FlairRoute(
                        subreddit = subreddit,
                        flair = flair
                    )
                )
            }
        )
    }
}