package com.elfen.redfun.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.domain.model.ResourceError
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.presentation.screens.details.PostDetailRoute
import com.elfen.redfun.presentation.screens.subreddit.SubredditRoute
import com.elfen.redfun.presentation.utils.plus
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@Composable
fun PostList(
    modifier: Modifier = Modifier,
    posts: LazyPagingItems<Post>,
    navController: NavController,
    displayMode: DisplayMode,
    sorting: Sorting? = null,
    navBarShown: Boolean? = null,
    onSelectSorting: (Sorting) -> Unit = {},
    onSelectDisplayMode: (DisplayMode) -> Unit = {},
    onNavBarShownChange: (Boolean) -> Unit = {},
    showSubreddit: Boolean = true,
    navigateFlair: (subreddit: String, flair: String) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    PostList(
        modifier = modifier,
        posts = posts,
        onNavigateToPost = { navController.navigate(PostDetailRoute(it.id)) },
        onNavigateToSubreddit = { navController.navigate(SubredditRoute(it)) },
        displayMode = displayMode,
        sorting = sorting,
        navBarShown = navBarShown,
        onSelectSorting = onSelectSorting,
        onSelectDisplayMode = onSelectDisplayMode,
        onNavBarShownChange = onNavBarShownChange,
        showSubreddit = showSubreddit,
        navigateFlair = navigateFlair,
        contentPadding = contentPadding
    )
}

@Composable
fun PostList(
    modifier: Modifier = Modifier,
    posts: LazyPagingItems<Post>,
    onNavigateToPost: (Post) -> Unit = {},
    onNavigateToSubreddit: (String) -> Unit = {},
    displayMode: DisplayMode,
    sorting: Sorting? = null,
    navBarShown: Boolean? = null,
    onSelectSorting: (Sorting) -> Unit = {},
    onSelectDisplayMode: (DisplayMode) -> Unit = {},
    onNavBarShownChange: (Boolean) -> Unit = {},
    showSubreddit: Boolean = true,
    navigateFlair: (subreddit: String, flair: String) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val scope = rememberCoroutineScope()
    val innerPadding = PaddingValues()
    val context = LocalContext.current
    val shouldMute by context.dataStore.data.mapNotNull {
        return@mapNotNull it[booleanPreferencesKey("shouldMute")]
    }.collectAsState(true)

    val listState = rememberLazyListState()
    val wrapperEnabled by remember { derivedStateOf { listState.firstVisibleItemIndex != 0 } }


    LazyColumn(
        state = listState,
        contentPadding = innerPadding + contentPadding,
        modifier = modifier
    ) {
        item {
            PostListPrefs(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                displayMode = displayMode,
                sorting = sorting,
                navBarShown = navBarShown,
                onSelectSorting = onSelectSorting,
                onSelectDisplayMode = onSelectDisplayMode,
                onToggleNavBar = { onNavBarShownChange(!(navBarShown ?: true)) }
            )
        }

        items(count = posts.itemCount) { index ->
            val post = posts[index]
            if (post != null) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PostCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToPost(post) }
                            .padding(16.dp),
                        post = post,
                        onNavigateSubreddit = {
                            onNavigateToSubreddit(post.subreddit)
                        },
                        onNavigateUserProfile = {
                            onNavigateToSubreddit("u_${post.author}")
                        },
                        onNavigateToFlair = {
                            navigateFlair(post.subreddit, it)
                        }
                    )
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f)
                )
            }
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
                        if (error.message != null)
                            Text(text = error.error.message ?: "Unknown error")
                    }
                }

            }
        } else if (posts.loadState.append is LoadState.NotLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            posts.refresh()
                        }
                    ) {
                        Text(text = "Load More")
                    }
                }
            }
        }
    }
}