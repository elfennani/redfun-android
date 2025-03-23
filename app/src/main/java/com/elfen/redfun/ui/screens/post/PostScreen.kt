package com.elfen.redfun.ui.screens.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elfen.redfun.domain.models.Comment
import com.elfen.redfun.ui.composables.PostCard
import kotlinx.serialization.Serializable

@Serializable
data class PostRoute(val id: String)

@Composable
fun PostScreen(navController: NavController, viewModel: PostViewModel = hiltViewModel()) {
    val state = viewModel.state.collectAsState()
    val post = state.value.post
    val comments = state.value.comments ?: emptyList()

    Scaffold { paddingValues ->
        if (post != null)

            LazyColumn(contentPadding = paddingValues) {
                item {
                    PostCard(post = post, truncate = false)
                }

                if (!state.value.isLoading)
                    items(comments) {
                        if (it is Comment.Body)
                            Text("Comment: ${it.body ?: it.id}")
                        else if (it is Comment.More)
                            Text("More: ${it.id}")
                    }
                else
                    item {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .align(alignment = androidx.compose.ui.Alignment.CenterHorizontally)
                            )
                        }
                    }
            }
        else if (state.value.isLoading) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(paddingValues)
                            .align(alignment = androidx.compose.ui.Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}