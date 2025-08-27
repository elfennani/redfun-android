package com.elfen.redfun.presentation.screens.details

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.domain.model.Comment
import com.elfen.redfun.presentation.components.CommentCard
import com.elfen.redfun.presentation.components.PostCard
import com.elfen.redfun.presentation.screens.subreddit.SubredditRoute
import com.elfen.redfun.presentation.utils.plus
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    val state = viewModel.state.collectAsState()
    val post = state.value.post
    val comments = state.value.comments ?: emptyList()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
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
            FloatingActionButton(onClick = {
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
        }
    ) { paddingValues ->
        if (post != null) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    item {
                        PostCard(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            post = post,
                            onClickSubreddit = {
                                navController.navigate(SubredditRoute(post.subreddit))
                            },
                            truncate = false
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