package com.elfen.redfun.presentation.screens.saved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.presentation.components.PostList
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
  navController: NavController,
  viewModel: SavedViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val displayMode by context.dataStore.data.map {
    val viewModelName = it[stringPreferencesKey("display_mode")];
    DisplayMode.valueOf(viewModelName ?: DisplayMode.MASONRY.name)
  }.collectAsState(DisplayMode.MASONRY)
    val navBarShown by context.dataStore.data.map {
        it[booleanPreferencesKey("nav_bar_shown")] ?: true
    }.collectAsState(true)
  val posts = viewModel.posts.collectAsLazyPagingItems()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text("Saved Posts")
        },
      )
    },
    contentWindowInsets = if (navBarShown) ScaffoldDefaults.contentWindowInsets.only(
      WindowInsetsSides.Top
    ) else WindowInsets(0.dp)
  ) {
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