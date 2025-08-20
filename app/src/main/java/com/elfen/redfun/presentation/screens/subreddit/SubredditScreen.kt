package com.elfen.redfun.presentation.screens.subreddit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.R
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.toLabel
import com.elfen.redfun.presentation.components.PostList
import com.elfen.redfun.presentation.screens.feed.components.SortingBottomSheet
import java.util.Locale

@Composable
fun SubredditScreen(
    viewModel: SubredditViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val subreddit = viewModel.route.name

    SubredditScreen(
        subreddit = subreddit,
        state = state,
        onEvent = viewModel::onEvent,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubredditScreen(
    subreddit: String,
    state: SubredditUiState,
    onEvent: (SubredditEvent) -> Unit,
    navController: NavController
) {
    val posts = state.posts?.collectAsLazyPagingItems()
    var sortingSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("r/$subreddit")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                actions = {

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(state.sorting.feed.replaceFirstChar {
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
                }
            )
        }
    ) {
        if (sortingSheet) {
            SortingBottomSheet(
                onDismissRequest = {
                    sortingSheet = false
                },
                onSelectSorting = { sorting ->
                    onEvent(SubredditEvent.UpdateSorting(sorting))
                },
                sorting = state.sorting,
            )
        }

        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.padding(it)
            ) {
                PostList(
                    posts = posts!!,
                    navController = navController,
                    displayMode = state.displayMode,
                    showSubreddit = false,
                )
            }
        }
    }
}