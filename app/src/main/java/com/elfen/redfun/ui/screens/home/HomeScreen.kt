package com.elfen.redfun.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elfen.redfun.ui.composables.PostCard
import com.elfen.redfun.ui.screens.post.PostRoute
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Serializable
data object HomeRoute

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        }
    ) { innerPadding ->
        val state by viewModel.state.collectAsState()
        if (state.isLoading && !state.isFetchingNextPage) {
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
            val listState = rememberLazyListState()
            val reachedBottom: Boolean by remember { derivedStateOf { listState.reachedBottom() } }

            LaunchedEffect (reachedBottom) {
                if (reachedBottom && !state.isFetchingNextPage) state.fetchNextPage()
            }

            LazyColumn(contentPadding = innerPadding, state = listState) {
                items(state.posts) {
                    PostCard(
                        modifier = Modifier.clickable { navController.navigate(PostRoute(it.id)) },
                        post = it
                    )
                }

                if (state.isFetchingNextPage) {
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
                } else if(state.isFetchingNextPageError){
                    item {
                        Column(
                            modifier = Modifier
                                .defaultMinSize(minHeight = 180.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Something went wrong!")
                            if (state.error != null)
                                Text(text = state.error!!)
                        }
                    }
                }
            }
        }
    }
}
