package com.elfen.redfun.presentation.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.R
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.toLabel
import com.elfen.redfun.presentation.components.PostList
import com.elfen.redfun.presentation.components.DisplayModeBottomSheet
import com.elfen.redfun.presentation.components.SortingBottomSheet
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun FeedScreen(navController: NavController, viewModel: FeedViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FeedScreen(
        state = state,
        onEvent = viewModel::onEvent,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedScreen(
    state: FeedUiState,
    onEvent: (FeedEvent) -> Unit,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val posts = state.posts?.collectAsLazyPagingItems()
    var sortingSheet by remember { mutableStateOf(false) }
    var viewModeSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (state.displayMode != DisplayMode.SCROLLER)
                TopAppBar(title = { Text("Feed") })
        },
        contentWindowInsets = if (state.isNavBarShown) ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Top
        ) else WindowInsets(0,0,0,0)
    ) { innerPadding ->

        if (state.isLoading || posts == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (viewModeSheet) {
                DisplayModeBottomSheet(
                    onDismissRequest = { viewModeSheet = false },
                    current = state.displayMode,
                    onSelectDisplayMode = { mode -> onEvent(FeedEvent.ChangeDisplayMode(mode)) }
                )
            }
            if (sortingSheet) {
                SortingBottomSheet(
                    onDismissRequest = { sortingSheet = false },
                    onSelectSorting = { onEvent(FeedEvent.ChangeSorting(it)) },
                    sorting = state.sorting,
                )
            }

            PostList(
                modifier = Modifier.padding(innerPadding),
                posts = posts,
                navController = navController,
                displayMode = state.displayMode,
                sorting = state.sorting,
                showSubreddit = true,
                onSelectSorting = { sorting -> onEvent(FeedEvent.ChangeSorting(sorting)) },
                onSelectDisplayMode = { mode -> onEvent(FeedEvent.ChangeDisplayMode(mode)) },
                onNavBarShownChange = { onEvent(FeedEvent.ToggleNavBar) },
                navBarShown = state.isNavBarShown,
            )
        }
    }
}
