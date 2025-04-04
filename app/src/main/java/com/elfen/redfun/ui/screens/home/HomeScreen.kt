package com.elfen.redfun.ui.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.elfen.redfun.ui.composables.PostCard
import com.elfen.redfun.ui.screens.post.PostRoute
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.domain.models.ResourceError
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.domain.models.SortingTime
import com.elfen.redfun.domain.models.toLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@Serializable
data object HomeRoute

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val posts = state.posts?.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val sortingSheetState = rememberModalBottomSheetState()
    var sortingSheet by remember { mutableStateOf(false) }

    val sortingTimeSheetState = rememberModalBottomSheetState()
    var sortingTimeSheet by remember { mutableStateOf(false) }
    var tempSorting by remember { mutableStateOf<Sorting?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    if (!state.isLoading)
                        TextButton(onClick = { sortingSheet = true }) {
                            Text(
                                (state.sorting ?: Sorting.Best).feed.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.US
                                    ) else it.toString()
                                })
                        }
                }
            )
        }
    ) { innerPadding ->
        val TAG = "HomeScreen"
        Log.d(TAG, "isLoading: ${state.isLoading}")
        Log.d(TAG, "isFetchingNextPage: ${state.isFetchingNextPage}")
        Log.d(TAG, "Posts: ${posts?.itemCount}")
        Log.d(TAG, "LoadState: ${posts?.loadState?.refresh}")
        Log.d(TAG, "======================================")

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
            if (sortingSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        sortingSheet = false
                    },
                    sheetState = sortingSheetState
                ) {
                    val updateSorting = { sorting: Sorting ->
                        state.onSortingChanged(sorting)
                        scope.launch { sortingSheetState.hide() }.invokeOnCompletion {
                            if (!sortingSheetState.isVisible) {
                                sortingSheet = false
                            }
                        }
                    }
                    val sortings = listOf(Sorting.Best, Sorting.Hot, Sorting.New, Sorting.Rising)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sortings.forEach {
                            TextButton(
                                onClick = { updateSorting(it) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(it.feed.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.US
                                    ) else it.toString()
                                })
                            }
                        }

                        TextButton(
                            onClick = {
                                tempSorting = Sorting.Top(SortingTime.ALL_TIME);
                                sortingTimeSheet = true
                                scope.launch { sortingSheetState.hide() }.invokeOnCompletion {
                                    if (!sortingSheetState.isVisible) {
                                        sortingSheet = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Top")
                        }


                        TextButton(
                            onClick = {
                                tempSorting = Sorting.Controversial(SortingTime.ALL_TIME);
                                sortingTimeSheet = true
                                scope.launch { sortingSheetState.hide() }.invokeOnCompletion {
                                    if (!sortingSheetState.isVisible) {
                                        sortingSheet = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Controversial")
                        }
                    }
                }
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
                        scope.launch { sortingSheetState.hide() }.invokeOnCompletion {
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

                                    scope.launch { sortingSheetState.hide() }.invokeOnCompletion {
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

            LazyColumn(contentPadding = innerPadding) {

                items(count = posts.itemCount) { index ->
                    val post = posts[index]
                    if (post != null)
                        PostCard(
                            modifier = Modifier.clickable { navController.navigate(PostRoute(post.id)) },
                            post = post
                        )
                }

                if (posts.loadState.append == LoadState.Loading) {
                    item {
                        Column(
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (posts.loadState.append is LoadState.Error) {
                    val error = (posts.loadState.append as LoadState.Error).error

                    if (error is ResourceError) {
                        item {
                            Column(
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 180.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Something went wrong!")
                                if (state.error != null)
                                    Text(text = error.error.message ?: "Unknown error")
                            }
                        }

                    }
                }
            }
        }
    }
}
