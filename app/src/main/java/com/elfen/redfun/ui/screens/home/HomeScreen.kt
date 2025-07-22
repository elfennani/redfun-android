package com.elfen.redfun.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.R
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.models.DisplayMode
import com.elfen.redfun.domain.models.ResourceError
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.domain.models.SortingTime
import com.elfen.redfun.domain.models.toLabel
import com.elfen.redfun.ui.composables.CompactPost
import com.elfen.redfun.ui.composables.PostContent
import com.elfen.redfun.ui.composables.PostList
import com.elfen.redfun.ui.screens.home.composables.DisplayModeBottomSheet
import com.elfen.redfun.ui.screens.home.composables.SortingBottomSheet
import com.elfen.redfun.ui.screens.post.PostRoute
import com.elfen.redfun.ui.screens.subreddit.SubredditRoute
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data object HomeRoute

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}


operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
    PaddingValues(
        start = this.calculateStartPadding(LayoutDirection.Ltr) + other.calculateStartPadding(
            LayoutDirection.Ltr
        ),
        end = this.calculateEndPadding(LayoutDirection.Ltr) + other.calculateEndPadding(
            LayoutDirection.Ltr
        ),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val posts = state.posts?.collectAsLazyPagingItems()
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val scope = rememberCoroutineScope()
    val sortingSheetState = rememberModalBottomSheetState()
    var sortingSheet by remember { mutableStateOf(false) }

    var viewModeSheet by remember { mutableStateOf(false) }

    val sortingTimeSheetState = rememberModalBottomSheetState()
    var sortingTimeSheet by remember { mutableStateOf(false) }
    var tempSorting by remember { mutableStateOf<Sorting?>(null) }

    val context = LocalContext.current
    val displayMode by context.dataStore.data.map {
        val viewModelName = it[stringPreferencesKey("display_mode")];
        DisplayMode.valueOf(viewModelName ?: DisplayMode.MASONRY.name)
    }.collectAsState(DisplayMode.MASONRY)

    Scaffold(
        topBar = {
            Box(){
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
                            Icon(painterResource(R.drawable.outline_view_quilt_24), null, modifier = Modifier.size(28.dp))
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
                                        is Sorting.Top -> " (${(state.sorting as Sorting.Top).time.toLabel()})"
                                        is Sorting.Controversial -> " (${(state.sorting as Sorting.Controversial).time.toLabel()})"
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
                                Icon(painterResource(R.drawable.outline_sort_24), null, modifier = Modifier.size(28.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    },
                )
            }
        }
    ) { innerPadding ->

        if ((state.isLoading && !state.isFetchingNextPage) || posts == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (state.isError && !state.isFetchingNextPageError) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Something went wrong!")
                if (state.error != null)
                    Text(text = state.error!!)
            }
        } else {
            if (viewModeSheet) {
                DisplayModeBottomSheet(
                    onDismissRequest = { viewModeSheet = false },
                    current = state.displayMode,
                    onSelectDisplayMode = { mode ->
                        state.onDisplayModeChanged(mode)
                    }
                )
            }
            if (sortingSheet) {
                SortingBottomSheet(
                    onDismissRequest = {
                        sortingSheet = false
                    },
                    onSelectSorting = {
                        state.onSortingChanged(it)
                    },
                    sorting = state.sorting,
                )
            }

            if (sortingTimeSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        sortingTimeSheet = false
                    },
                    sheetState = sortingTimeSheetState
                ) {
                    val updateSorting = { sorting: Sorting ->

                        state.onSortingChanged(sorting)
                        scope.launch {
                            lazyStaggeredGridState.scrollToItem(0)
                            sortingSheetState.hide()
                        }.invokeOnCompletion {
                            if (!sortingSheetState.isVisible) {
                                sortingSheet = false
                            }
                        }
                    }
                    val times = SortingTime.entries.toTypedArray()

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        times.forEach {
                            TextButton(
                                onClick = {
                                    if (tempSorting != null) {
                                        if (tempSorting is Sorting.Top) {
                                            updateSorting(Sorting.Top(it))
                                        } else if (tempSorting is Sorting.Controversial) {
                                            updateSorting(Sorting.Controversial(it))
                                        }
                                    }

                                    scope.launch { sortingSheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sortingSheetState.isVisible) {
                                                sortingTimeSheet = false
                                            }
                                        }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(it.toLabel().replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.US
                                    ) else it.toString()
                                })
                            }
                        }
                    }
                }
            }

            PostList(
                modifier = Modifier.padding(innerPadding),
                posts = posts,
                navController = navController,
                displayMode = displayMode,
                showSubreddit = true,
            )
        }
    }
}
