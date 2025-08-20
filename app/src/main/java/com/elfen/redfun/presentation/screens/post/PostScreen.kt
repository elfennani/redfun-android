package com.elfen.redfun.presentation.screens.post

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elfen.redfun.R
import com.elfen.redfun.presentation.components.CommentCard
import kotlinx.serialization.Serializable
import androidx.core.net.toUri
import com.elfen.redfun.presentation.components.PostCard
import com.elfen.redfun.presentation.screens.subreddit.SubredditRoute

@Serializable
data class PostRoute(val id: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController, viewModel: PostViewModel = hiltViewModel()) {
    val state = viewModel.state.collectAsState()
    val post = state.value.post
    val comments = state.value.comments ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Post")
                },
                actions = {
                    IconButton(
                        onClick = {
                            post?.url?.let { url ->
                                val intent =
                                    Intent(Intent.ACTION_VIEW, "https://www.reddit.com$url".toUri())
                                navController.context.startActivity(intent)
                            }

                        }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.baseline_open_in_new_24),
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (post != null)

            LazyColumn(contentPadding = paddingValues) {
                item {
                    PostCard (
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        post = post,
                        onClickSubreddit = {
                            navController.navigate(SubredditRoute(post.subreddit))
                        },
                        truncate = false
                    )
                }

                if (!state.value.isLoading)
                    items(comments) {
                        CommentCard(it)
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