package com.elfen.redfun.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.elfen.redfun.domain.models.DisplayMode
import com.elfen.redfun.domain.models.Post
import com.elfen.redfun.domain.models.ResourceError
import com.elfen.redfun.ui.screens.home.plus
import com.elfen.redfun.ui.screens.post.PostRoute
import com.elfen.redfun.ui.screens.subreddit.SubredditRoute

@Composable
fun PostList(
  modifier: Modifier = Modifier,
  posts: LazyPagingItems<Post>,
  navController: NavController,
  displayMode: DisplayMode,
  lazyStaggeredGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
  showSubreddit: Boolean = true
) {
  val innerPadding = PaddingValues(0.dp)

  if (displayMode == DisplayMode.SCROLLER) {
    val pagerState = rememberPagerState(pageCount = { posts.itemCount })

    VerticalPager(
      state = pagerState,
      modifier = modifier.fillMaxSize(),
      contentPadding = innerPadding
    ) { page ->
      val post = posts[page]
      if (post != null) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          PostContent(
            post = post,
            autoPlay = true,
            onClick = {
              navController.navigate(PostRoute(post.id))
            }
          )
        }
      }
    }
  } else if(displayMode == DisplayMode.LIST){
    LazyColumn(
      contentPadding = innerPadding,
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = modifier
    ) {
      items(count = posts.itemCount) { index ->
        val post = posts[index]
        if (post != null) {
          PostCard (
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .clickable { navController.navigate(PostRoute(post.id)) }
              .padding(16.dp),
            post = post,
            onClickSubreddit = {
              navController.navigate(SubredditRoute(post.subreddit))
            },
            showSubreddit = showSubreddit
          )
        }
      }

      if (posts.loadState.append == LoadState.Loading) {
        item{
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
      } else if (posts.loadState.append is LoadState.Error) {
        val error = (posts.loadState.append as LoadState.Error).error

        if (error is ResourceError) {
          item{
            Column(
              modifier = Modifier
                .defaultMinSize(minHeight = 180.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "Something went wrong!")
              if (error.message != null)
                Text(text = error.error.message ?: "Unknown error")
            }
          }

        }
      }
    }
  }
  else {
    LazyVerticalStaggeredGrid(
      contentPadding = innerPadding + PaddingValues(16.dp),
      columns = StaggeredGridCells.Fixed(2),
      verticalItemSpacing = 16.dp,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      state = lazyStaggeredGridState,
      modifier = modifier
    ) {

      items(count = posts.itemCount) { index ->
        val post = posts[index]
        if (post != null) {
          CompactPost(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .clickable { navController.navigate(PostRoute(post.id)) },
            post = post,
            onClickSubreddit = {
              navController.navigate(SubredditRoute(post.subreddit))
            },
            showSubreddit = showSubreddit
          )
        }
      }

      if (posts.loadState.append == LoadState.Loading) {
        item(span = StaggeredGridItemSpan.FullLine) {
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
      } else if (posts.loadState.append is LoadState.Error) {
        val error = (posts.loadState.append as LoadState.Error).error

        if (error is ResourceError) {
          item(span = StaggeredGridItemSpan.FullLine) {
            Column(
              modifier = Modifier
                .defaultMinSize(minHeight = 180.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "Something went wrong!")
              if (error.message != null)
                Text(text = error.error.message ?: "Unknown error")
            }
          }

        }
      }
    }
  }
}