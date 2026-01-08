package com.elfen.redfun.presentation.screens.details

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elfen.redfun.R
import com.elfen.redfun.domain.model.Comment
import com.elfen.redfun.presentation.components.CommentCard
import com.elfen.redfun.presentation.components.PostCard
import com.elfen.redfun.presentation.components.VideoPlayer
import com.elfen.redfun.presentation.components.ui.AppActionButton
import com.elfen.redfun.presentation.components.ui.AppNavigationBarIconAction
import com.elfen.redfun.presentation.screens.flair.FlairRoute
import com.elfen.redfun.presentation.screens.subreddit.SubredditRoute
import com.elfen.redfun.presentation.utils.LocalScaffoldPadding
import com.elfen.redfun.presentation.utils.rememberPlayerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    val player = viewModel.exoPlayer
    val state = viewModel.state.collectAsState()
    val post = state.value.post
    val comments = state.value.comments ?: emptyList()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val outerPadding = LocalScaffoldPadding.current
    val visibleIndex by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex
        }
    }
    val topComments =
        comments.filterIsInstance<Comment.Body>()
            .foldIndexed(emptyList<Pair<Int, Comment.Body>>()) { index, acc, comment ->
                if (comment.depth == 0) acc + (index to comment) else acc
            }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Post")
                },
                actions = {
                    IconButton(onClick = { viewModel.downloadPost() }) {
                        Icon(Icons.Default.Download, null)
                    }
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
        },
        floatingActionButton = {
            AppActionButton(onClick = {
                scope.launch {
                    val TAG = "PostDetailScreen"
                    if (lazyListState.firstVisibleItemIndex == 0) {
                        Log.d(TAG, "Scrolling to first comment")
                        lazyListState.animateScrollToItem(1)
                    } else {
                        val currentCommentIndex = lazyListState.firstVisibleItemIndex - 1
                        val nextComment =
                            topComments.find { it.first > currentCommentIndex }

                        if (nextComment != null)
                            lazyListState.animateScrollToItem(
                                nextComment.first + 1,
                            )
                    }
                }
            }) {
                Icon(Icons.Default.ArrowDownward, null)
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(WindowInsets(bottom = outerPadding.calculateBottomPadding()))
    ) { paddingValues ->
        if (post != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = paddingValues
                ) {
                    item {
                        PostCard(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            post = post,
                            exoPlayer = player,
                            autoPlay = true,
                            navigateSubreddit = {
                                navController.navigate(SubredditRoute(post.subreddit))
                            },
                            truncate = false,
                            navigateToFlair = { flair ->
                                navController.navigate(
                                    FlairRoute(
                                        subreddit = post.subreddit,
                                        flair = flair
                                    )
                                )
                            }
                        )
                    }

                    if (!state.value.isLoading)
                        items(comments, key = {
                            if (it is Comment.Body) it.id else (it as Comment.More).id
                        }) {
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
                                        .align(alignment = Alignment.CenterHorizontally)
                                )
                            }
                        }
                }

                val firstVisibleIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
                if (post.video?.source != null && firstVisibleIndex > 1)
                    VideoPlayer(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset((24).dp, (-16).dp - paddingValues.calculateBottomPadding())
                            .width(256.dp)
                            .aspectRatio(post.video.width.toFloat() / post.video.height)
                            .shadow(8.dp, MaterialTheme.shapes.medium),
                        exoPlayer = player
                    )
            }
        } else if (state.value.isLoading) {
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