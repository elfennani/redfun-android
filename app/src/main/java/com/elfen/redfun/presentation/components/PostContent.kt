package com.elfen.redfun.presentation.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.data.rememberSettings
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.presentation.utils.isWifiNetwork

@Composable
fun rememberExoPlayer(
    post: Post,
    muted: Boolean = false
): ExoPlayer {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            volume = if (muted) 0f else 1f
            if (post.video != null)
                setMediaItem(androidx.media3.common.MediaItem.fromUri(post.video.source))
            prepare()
        }
    }

    LaunchedEffect(key1 = post.video) {
        if (post.video != null) {
            player.setMediaItem(androidx.media3.common.MediaItem.fromUri(post.video.source))
            player.prepare()
            player.playWhenReady = true
        }
    }

    DisposableEffect(key1 = player) {
        onDispose {
            player.release()
        }
    }

    return player
}

@Composable
fun rememberIsPlaying(player: ExoPlayer): Boolean {
    val isPlaying = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = player) {
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                isPlaying.value = isPlayingValue
            }
        })
    }

    return isPlaying.value
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberVideoPositionPercent(
    player: ExoPlayer,
): Float {
    val position = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = player) {
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == androidx.media3.common.Player.STATE_READY) {
                    val duration = player.duration.toFloat()
                    if (duration > 0) {
                        position.floatValue = player.currentPosition / duration
                    }
                }
            }
        })
    }

    return position.floatValue
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostContent(
    modifier: Modifier = Modifier,
    post: Post,
    onClick: () -> Unit,
    autoPlay: Boolean = false,
    player: ExoPlayer = rememberExoPlayer(post),
    isScroller: Boolean = false
) {
    val context = LocalContext.current;
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .fillMaxHeight(if (isScroller) 1f else 0f),
        verticalArrangement = Arrangement.spacedBy(4.dp, alignment = if(isScroller) Alignment.CenterVertically else Alignment.Top)
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
            var videoEnabled by remember { mutableStateOf(autoPlay) }
            if (!videoEnabled) {
                Box(
                    modifier = Modifier
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
                val isPlaying = rememberIsPlaying(player)
                val settings by rememberSettings()

                LaunchedEffect(settings) {
                    if (settings != null) {
                        val isWifi = isWifiNetwork(context)
                        val maxResolution =
                            if (isWifi) settings!!.maxWifiResolution else settings!!.maxMobileResolution
                        player.trackSelectionParameters = player.trackSelectionParameters
                            .buildUpon()
                            .setMaxVideoSize(maxResolution, maxResolution)
                            .build()
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if (player.isPlaying) {
                                player.pause()
                            } else {
                                player.play()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val aspectRatio = post.video.width.toFloat() / post.video.height.toFloat()
                    if (isScroller)
                        AndroidView(
                            modifier = Modifier
                                .fillMaxSize(),
                            factory = { context ->
                                PlayerView(context).apply {
                                    this.player = player
                                    this.useController = false
                                    this.resizeMode =
                                        if (aspectRatio < 0.75f) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
                                }
                            },
                            update = { playerView ->
                                playerView.player = player
                            }
                        )
                    else
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
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
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxWidth()
                        .aspectRatio(image.width.toFloat() / image.height.toFloat())
                )
            } else {
                val pagerState = rememberPagerState(pageCount = {
                    post.images.size
                })
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) { page ->
                        val image = post.images[page]
                        if (isScroller)
                            AsyncImage(
                                model = image.source,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        else
                            AsyncImage(
                                model = image.source,
                                contentDescription = null,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .fillMaxWidth()
                                    .aspectRatio(image.width.toFloat() / image.height.toFloat())
                            )
                    }

                    if (!isScroller)
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
        }
    }
}