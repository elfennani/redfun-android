package com.elfen.redfun.ui.screens.subreddit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
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
          TextButton(onClick = { sortingSheet = true }) {
            Text(
              (sorting).feed.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                  Locale.US
                ) else it.toString()
              } + when (sorting) {
                is Sorting.Top -> " (${(sorting as Sorting.Top).time.toLabel()})"
                is Sorting.Controversial -> " (${(sorting as Sorting.Controversial).time.toLabel()})"
                else -> ""
              },
            )
          }
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
      )
    }
  }
}