package com.elfen.redfun.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elfen.redfun.ui.composables.PostCompact
import com.elfen.redfun.ui.screens.post.PostRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        }
    ) { innerPadding ->
        val posts = viewModel.posts.collectAsState()
        LazyColumn(contentPadding = innerPadding) {
            items(posts.value) {
                PostCompact(
                    modifier = Modifier.clickable { navController.navigate(PostRoute(it.id)) },
                    post = it
                )
            }
        }
    }
}
