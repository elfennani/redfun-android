package com.elfen.redfun.ui.screens.subreddit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.R
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.models.DisplayMode
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.domain.models.toLabel
import com.elfen.redfun.ui.composables.PostList
import com.elfen.redfun.ui.screens.home.composables.SortingBottomSheet
import kotlinx.coroutines.flow.map
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubredditScreen(
    viewModel: SubredditViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val displayMode by context.dataStore.data.map {
        val viewModelName = it[stringPreferencesKey("display_mode")];
        DisplayMode.valueOf(viewModelName ?: DisplayMode.MASONRY.name)
    }.collectAsState(DisplayMode.MASONRY)
    val posts = viewModel.posts.collectAsLazyPagingItems()
    var sortingSheet by remember { mutableStateOf(false) }
    val sorting by viewModel.sorting.collectAsState(Sorting.Hot)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("r/${viewModel.route.name}")
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
                                Text((sorting).feed.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.US
                                    ) else it.toString()
                                } + when (sorting) {
                                    is Sorting.Top -> " (${(sorting as Sorting.Top).time.toLabel()})"
                                    is Sorting.Controversial -> " (${(sorting as Sorting.Controversial).time.toLabel()})"
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
                onSelectSorting = {
                    viewModel.updateSorting(it)
                },
                sorting = sorting,
            )
        }

        Column(
            modifier = Modifier.padding(it)
        ) {
            PostList(
                posts = posts,
                navController = navController,
                displayMode = displayMode,
                showSubreddit = false,
            )
        }
    }
}