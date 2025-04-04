package com.elfen.redfun.ui.composables

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.domain.models.Post

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostContent(modifier: Modifier = Modifier, post: Post, onClick: () -> Unit) {
    val context = LocalContext.current;
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (post.link !== null && post.video == null) {
            Row(
                modifier = Modifier
                    .border(1.dp, color = Color.LightGray, CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        try {
                            context.startActivity(
                                android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    post.link.toUri()
                                )
                            )
                        } catch (e: Exception) {
                            Log.e("PostCompact", "Error opening link", e)
                        }
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = post.link,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painterResource(id = R.drawable.baseline_link_24),
                    contentDescription = null,
                    tint = Color.Gray
                )

            }
        } else if (post.video != null) {
            var videoEnabled by remember { mutableStateOf(false) }
            if (!videoEnabled) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat())
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .combinedClickable(onLongClick = { onClick() }, onClick = {
                            videoEnabled = true
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = post.thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat()),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    }
                }
            } else {
                val player =
                    remember { androidx.media3.exoplayer.ExoPlayer.Builder(context).build() }
                var isPlaying by remember { mutableStateOf(true) }

                LaunchedEffect(key1 = player) {
                    player.setMediaItem(androidx.media3.common.MediaItem.fromUri(post.video.source))
                    player.prepare()
                    player.play()
                }

                LaunchedEffect(key1 = player) {
                    player.addListener(object : androidx.media3.common.Player.Listener {
                        override fun onIsPlayingChanged(value: Boolean) {
                            isPlaying = value
                        }
                    })
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (player.isPlaying) {
                                player.pause()
                            } else {
                                player.play()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .aspectRatio(
                                post.video.width.toFloat() / post.video.height.toFloat()
                            ),
                        factory = { context ->
                            PlayerView(context).apply {
                                this.player = player
                                this.useController = false
                            }
                        },
                        update = { playerView ->
                            playerView.player = player
                        }
                    )

                    if (!isPlaying) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        } else if (!post.images.isNullOrEmpty()) {
            if (post.images.size == 1) {
                val image = post.images.first()
                AsyncImage(
                    model = image.source,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxWidth()
                        .aspectRatio(image.width.toFloat() / image.height.toFloat())
                )
            } else {
                val pagerState = rememberPagerState(pageCount = {
                    post.images.size
                })
                Box {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) { page ->
                        val image = post.images[page]
                        AsyncImage(
                            model = image.source,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(image.width.toFloat() / image.height.toFloat())
                        )
                    }

                    Icon(
                        painterResource(R.drawable.baseline_photo_library_24),
                        null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopStart)
                            .offset(16.dp, 16.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    post.title,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    lineHeight = 21.sp
                )
                if (!post.body.isNullOrEmpty())
                    Text(
                        post.body,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
            }
        }
    }
}