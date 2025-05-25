package com.elfen.redfun.ui.screens.home

import com.elfen.redfun.R
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.elfen.redfun.domain.models.ResourceError
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.domain.models.SortingTime
import com.elfen.redfun.domain.models.toLabel
import com.elfen.redfun.ui.composables.PostContent
import com.elfen.redfun.ui.composables.Skeleton
import com.elfen.redfun.ui.screens.sessions.SessionRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@Serializable
data object HomeRoute

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}


private operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
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

    val viewModeSheetState = rememberModalBottomSheetState()
    var viewModeSheet by remember { mutableStateOf(false) }

    val sortingTimeSheetState = rememberModalBottomSheetState()
    var sortingTimeSheet by remember { mutableStateOf(false) }
    var tempSorting by remember { mutableStateOf<Sorting?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sidebarState by viewModel.sidebarState.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                if (sidebarState.isLoading) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .width(270.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Skeleton(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Skeleton(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Skeleton(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(24.dp)
                            )
                            Skeleton(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(18.dp)
                            )
                        }
                        HorizontalDivider()
                        Column {
                            TextButton(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                                Icon(painterResource(R.drawable.baseline_bookmark_24), null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Saved", modifier = Modifier.weight(1f))
                            }
                            TextButton(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Settings, null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Settings", modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .width(270.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = sidebarState.user?.icon,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            IconButton(onClick = { navController.navigate(SessionRoute) }) {
                                Icon(painterResource(R.drawable.baseline_switch_account_24), null)
                            }
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                if (!sidebarState.user!!.fullname.isNullOrEmpty()) sidebarState.user!!.fullname!! else "u/${sidebarState.user!!.username}",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (sidebarState.user!!.fullname.isNullOrEmpty())
                                Text(
                                    "u/${sidebarState.user!!.username}",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 18.sp,
                                        color = androidx.compose.ui.graphics.Color.Gray
                                    )
                                )
                        }
                        HorizontalDivider()
                        Column {
                            TextButton(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                                Icon(painterResource(R.drawable.baseline_bookmark_24), null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Saved", modifier = Modifier.weight(1f))
                            }
                            TextButton(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Settings, null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Settings", modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Home") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch { viewModeSheet = true }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.List, null)
                        }
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
                    ModalBottomSheet(
                        onDismissRequest = {
                            viewModeSheet = false
                        },
                        sheetState = viewModeSheetState
                    ) {
                        ViewMode.entries.forEach {
                            TextButton(onClick = {
                                state.onViewModeChanged(it)
                                scope.launch { viewModeSheetState.hide() }.invokeOnCompletion {
                                    if (!viewModeSheetState.isVisible) {
                                        viewModeSheet = false
                                    }
                                }
                            }, enabled = state.viewMode != it) {
                                Text(it.label)
                            }
                        }
                    }
                }
                if (sortingSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            sortingSheet = false
                        },
                        sheetState = sortingSheetState
                    ) {
                        val updateSorting = { sorting: Sorting ->
                            state.onSortingChanged(sorting)
                            scope.launch {
                                sortingSheetState.hide()
                                lazyStaggeredGridState.scrollToItem(0)
                            }.invokeOnCompletion {
                                if (!sortingSheetState.isVisible) {
                                    sortingSheet = false
                                }
                            }
                        }
                        val sortings =
                            listOf(Sorting.Best, Sorting.Hot, Sorting.New, Sorting.Rising)

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

                if (state.viewMode == ViewMode.SCROLLER) {
                    val pagerState = rememberPagerState(pageCount = { posts.itemCount })

                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding
                    ) { page ->
                        val post = posts[page]
                        if (post != null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                PostContent(
                                    post = post,
                                    autoPlay = true,
                                    onClick = {
                                        navController.navigate(PostRoute(post.id))
                                    }
                                )
                            }
                        }
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        contentPadding = innerPadding + PaddingValues(16.dp),
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = when (state.viewMode) {
                            ViewMode.MASONRY -> 8.dp
                            ViewMode.MASONRY_DETAILED -> 24.dp
                            ViewMode.SCROLLER -> 0.dp
                        },
                        horizontalArrangement = Arrangement.spacedBy(
                            when (state.viewMode) {
                                ViewMode.MASONRY -> 8.dp
                                ViewMode.MASONRY_DETAILED -> 12.dp
                                ViewMode.SCROLLER -> 0.dp
                            }
                        ),
                        state = lazyStaggeredGridState
                    ) {

                        items(count = posts.itemCount) { index ->
                            val post = posts[index]
                            if (post != null) {
                                if (state.viewMode == ViewMode.MASONRY_DETAILED) {
                                    PostCard(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable { navController.navigate(PostRoute(post.id)) },
                                        post = post
                                    )
                                } else {
                                    PostContent(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp)),
                                        post = post,
                                        onClick = {
                                            navController.navigate(
                                                PostRoute(post.id)
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        if (posts.loadState.append == LoadState.Loading) {
                            item(span = StaggeredGridItemSpan.FullLine) {
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
                                item(span = StaggeredGridItemSpan.FullLine) {
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
    }
}
