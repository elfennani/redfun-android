package com.elfen.redfun.presentation.screens.flair

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.presentation.components.PostList
import com.elfen.redfun.presentation.screens.details.PostDetailRoute
import com.elfen.redfun.presentation.screens.feed.FeedEvent
import com.elfen.redfun.presentation.screens.subreddit.SubredditRoute
import com.elfen.redfun.presentation.theme.AppTheme

@Composable
fun FlairScreen(navController: NavController) {
    val viewModel = hiltViewModel<FlairViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    FlairScreen(
        state = state.value,
        onEvent = viewModel::onEvent,
        navigateBack = { navController.popBackStack() },
        navigateToPost = { navController.navigate(PostDetailRoute(it)) },
        navigateToSubreddit = { navController.navigate(SubredditRoute(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlairScreen(
    state: FlairUiState,
    onEvent: (FlairEvent) -> Unit = {},
    navigateBack: () -> Unit = { },
    navigateToPost: (String) -> Unit = { },
    navigateToSubreddit: (String) -> Unit = { },
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                title = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = state.flair, style = MaterialTheme.typography.titleSmall)
                    }
                }
            )
        }
    ) { paddingValues ->
        val posts = state.posts.collectAsLazyPagingItems()

        PostList(
            modifier = Modifier.padding(paddingValues),
            posts = posts,
            displayMode = state.displayMode,
            sorting = state.sorting,
            showSubreddit = true,
            onSelectSorting = { sorting -> onEvent(FlairEvent.ChangeSorting(sorting)) },
            onSelectDisplayMode = { mode -> onEvent(FlairEvent.ChangeDisplayMode(mode)) },
            onNavBarShownChange = { onEvent(FlairEvent.ToggleNavBar) },
            navBarShown = state.navBarShown,
            navigateToPost = {
                navigateToPost(it.id)
            },
            navigateToSubreddit = {
                navigateToSubreddit(it)
            }
        )
    }
}

@Preview
@Composable
private fun FlairScreenPreview() {
    AppTheme {
        FlairScreen(
            state = FlairUiState(
                flair = "Example Flair"
            )
        )
    }
}