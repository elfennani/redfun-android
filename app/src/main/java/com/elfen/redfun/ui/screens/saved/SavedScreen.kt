package com.elfen.redfun.ui.screens.saved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.elfen.redfun.domain.models.DisplayMode
import com.elfen.redfun.ui.composables.PostList
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.getValue

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
  val posts = viewModel.posts.collectAsLazyPagingItems()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text("Saved Posts")
        },
        navigationIcon = {
          IconButton(
            onClick = { navController.popBackStack() }
          ) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
          }
        },

      )
    }
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