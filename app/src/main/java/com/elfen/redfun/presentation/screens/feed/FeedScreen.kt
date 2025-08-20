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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.R
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.toLabel
import com.elfen.redfun.presentation.components.PostList
import com.elfen.redfun.presentation.screens.feed.components.DisplayModeBottomSheet
import com.elfen.redfun.presentation.screens.feed.components.SortingBottomSheet
import kotlinx.coroutines.flow.map
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
    val posts = state.posts?.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    var sortingSheet by remember { mutableStateOf(false) }
    var viewModeSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (state.displayMode == DisplayMode.SCROLLER) return@Scaffold
            TopAppBar(
                title = { Text("Feed") },
                actions = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                            .size(44.dp)
                            .clickable {
                                scope.launch { viewModeSheet = true }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painterResource(R.drawable.outline_view_quilt_24),
                            null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text((state.sorting ?: Sorting.Best).feed.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.US
                                    ) else it.toString()
                                } + when (state.sorting) {
                                    is Sorting.Top -> " (${state.sorting.time.toLabel()})"
                                    is Sorting.Controversial -> " (${state.sorting.time.toLabel()})"
                                    else -> ""
                                })
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                                .size(44.dp)
                                .clickable {
                                    sortingSheet = true
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painterResource(R.drawable.outline_sort_24),
                                null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
            )
        },
        floatingActionButton = {
            if (state.displayMode == DisplayMode.SCROLLER) {
                Column(
                    modifier = Modifier
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(onClick = {
                        scope.launch {
                            viewModeSheet = true
                        }
                    }) {
                        Icon(
                            painterResource(R.drawable.outline_view_quilt_24),
                            contentDescription = "Change Display Mode",
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                sortingSheet = true
                            }
                        },
                    ) {
                        Icon(
                            painterResource(R.drawable.outline_sort_24),
                            contentDescription = "Change Sorting",
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                onEvent(FeedEvent.ToggleNavBar)
                            }
                        },
                    ) {
                        Icon(
                            if (state.isNavBarShown) Icons.Default.ArrowDropDown
                            else Icons.Default.ArrowDropUp,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        contentWindowInsets = if (state.isNavBarShown) ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Top
        ) else WindowInsets(0.dp)
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
                showSubreddit = true,
            )
        }
    }
}
