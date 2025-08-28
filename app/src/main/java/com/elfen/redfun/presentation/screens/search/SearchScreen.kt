package com.elfen.redfun.presentation.screens.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.presentation.components.PostList
import com.elfen.redfun.presentation.screens.flair.FlairRoute
import com.elfen.redfun.presentation.screens.search.components.ProfileSearchItem
import com.elfen.redfun.presentation.screens.search.components.SubredditSearchItem
import com.elfen.redfun.presentation.screens.subreddit.SubredditRoute
import com.elfen.redfun.presentation.theme.AppTheme
import com.elfen.redfun.presentation.utils.plus

@Composable
fun SearchScreen(navController: NavHostController) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        navController = navController,
        onEvent = viewModel::onEvent,
        onNavigate = navController::navigate,
        onBack = { navController.popBackStack() }
    )
}

private const val TAG = "SearchScreen"

@Composable
private fun SearchScreen(
    state: SearchUiState = SearchUiState(),
    navController: NavHostController,
    onEvent: (SearchEvent) -> Unit = {},
    onNavigate: (Any) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val posts = state.posts.collectAsLazyPagingItems()
    var isFocused by rememberSaveable { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        if (state.searchedQuery.isEmpty())
            focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                    BasicTextField(
                        value = state.query,
                        onValueChange = { onEvent(SearchEvent.OnQueryChange(it)) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .focusRequester(focusRequester)
                            .onFocusChanged { isFocused = it.isFocused },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                onEvent(SearchEvent.Search(state.query))
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            imeAction = ImeAction.Search
                        ),
                        decorationBox = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (state.selectedSubreddit != null) {
                                    Row(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable { onEvent(SearchEvent.SelectSubreddit(null)) }
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                CircleShape
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            "r/${state.selectedSubreddit}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Icon(
                                            Icons.Default.Close,
                                            null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier
                                                .size(16.dp)
                                        )
                                    }
                                }
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (state.query.isEmpty()) {
                                        Text(
                                            "Search",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    it()
                                }
                            }
                        }
                    )
                }
            }
        },
        contentWindowInsets = state.let {
            if (it.isNavBarShown && it.displayMode != DisplayMode.SCROLLER) {
                ScaffoldDefaults.contentWindowInsets.only(
                    WindowInsetsSides.Top
                )
            } else {
                WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
            }
        }
    ) { paddingValues ->
        state.let {
            Log.d(
                "SearchScreen",
                "SearchScreen: ${it.isNavBarShown && it.displayMode != DisplayMode.SCROLLER}"
            )
        }
        Log.d("SearchScreen", "SearchScreen: ${paddingValues.calculateTopPadding()}")
        if (isFocused && state.selectedSubreddit == null) {
            LazyColumn(
                contentPadding = paddingValues + PaddingValues(
                    vertical = 16.dp,
                    horizontal = 8.dp
                ) + WindowInsets.ime.asPaddingValues(),
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (state.autoCompleteResult != null) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Communities", style = MaterialTheme.typography.titleMedium)
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(16.dp)
                                    .alpha(if (state.isLoading) 1f else 0f),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                    }
                    if (state.autoCompleteResult.subreddits.isEmpty()) {
                        item {
                            Text(
                                "No communities found",
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(state.autoCompleteResult.subreddits) {
                        SubredditSearchItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigate(SubredditRoute(it.name)) }
                                .padding(8.dp),
                            subreddit = it,
                            onSelectSubreddit = { name -> onEvent(SearchEvent.SelectSubreddit(name)) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Users",
                                style = MaterialTheme.typography.titleMedium
                            )
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(16.dp)
                                    .alpha(if (state.isLoading) 1f else 0f),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                    }

                    if (state.autoCompleteResult.users.isEmpty()) {
                        item {
                            Text(
                                "No users found",
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(state.autoCompleteResult.users) {
                        ProfileSearchItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {}
                                .padding(8.dp),
                            profile = it
                        )
                    }
                } else if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        } else {
            PostList(
                posts = posts,
                navController = navController,
                displayMode = state.displayMode,
                sorting = state.sorting,
                modifier = Modifier.padding(paddingValues),
                navBarShown = state.isNavBarShown,
                onSelectSorting = { sorting -> onEvent(SearchEvent.ChangeSorting(sorting)) },
                onSelectDisplayMode = { mode -> onEvent(SearchEvent.ChangeDisplayMode(mode)) },
                onNavBarShownChange = { onEvent(SearchEvent.ToggleNavBar) },
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
}

@Preview
@Composable
private fun SearchScreenPreview() {
    AppTheme {
        SearchScreen(
            navController = rememberNavController()
        )
    }
}