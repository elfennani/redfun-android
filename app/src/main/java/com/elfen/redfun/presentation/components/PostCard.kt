package com.elfen.redfun.presentation.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.data.rememberSettings
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.presentation.utils.isWifiNetwork
import kotlin.time.ExperimentalTime

@SuppressLint("DefaultLocale")
fun Long.shortenNumber(): String {
    return when {
        this >= 1_000_000_000 -> String.format("%.1fB", this / 1_000_000_000.0)
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
        else -> this.toString()
    }.replace(".0", "") // Remove trailing ".0" if not needed
}

@OptIn(ExperimentalTime::class)
@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    post: Post,
    showSubreddit: Boolean = true,
    truncate: Boolean = true,
    navigateSubreddit: () -> Unit,
    navigateToFlair: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(12.dp)

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(CircleShape)
                .clickable { navigateSubreddit() }
        ) {
            AsyncImage(
                model = post.subredditIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy((-2).dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start
            ) {
                if (showSubreddit) {
                    Text(
                        "r/${post.subreddit}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Text(
                        "u/${post.author} · ${formatDistanceToNowStrict(post.created)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
        }
        Text(
            post.title,
            overflow = TextOverflow.Ellipsis,
            maxLines = if (truncate) 2 else Int.MAX_VALUE,
            style = MaterialTheme.typography.titleMedium,
        )
        if(post.nsfw || post.flair != null){
            PostFlairs(
                isNSFW = post.nsfw,
                flair = post.flair,
                navigateFlair = navigateToFlair
            )
        }
        if (!post.body.isNullOrEmpty()) {
            if (truncate)
                Text(
                    post.body,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            else {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                    MarkdownRenderer(content = post.body)
                }
            }
        }
        if (post.video != null) {
            var videoEnabled by remember { mutableStateOf(false) }
            if (!videoEnabled) {
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat())
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { videoEnabled = true },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = post.thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape)
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
                val settings by rememberSettings()
                val player =
                    remember { ExoPlayer.Builder(context).build() }
                var isPlaying by remember { mutableStateOf(true) }

                LaunchedEffect(key1 = player) {
                    player.setMediaItem(androidx.media3.common.MediaItem.fromUri(post.video.source))
                    player.prepare()
                    player.playWhenReady = true

                    player.addListener(object : androidx.media3.common.Player.Listener {
                        override fun onIsPlayingChanged(value: Boolean) {
                            isPlaying = value
                        }
                    })
                }

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

                DisposableEffect(key1 = player) {
                    onDispose {
                        player.release()
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
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
                            .clip(shape)
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
        }

        if (!post.images.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape),
                contentAlignment = Alignment.BottomStart
            ) {
                if (post.images.size == 1) {
                    val image = post.images.first()
                    AsyncImage(
                        model = image.source,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape)
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
                            modifier = Modifier.clip(shape)
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

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(8.dp, 8.dp)
                                .background(Color.Black.copy(alpha = 0.33f), CircleShape)
                                .padding(4.dp)
                        ) {
                            Icon(
                                painterResource(
                                    R.drawable.baseline_photo_library_24
                                ),
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        CircleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    painterResource(R.drawable.baseline_arrow_upward_24),
                    "upvote",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    post.score.toLong().shortenNumber(),
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        CircleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.Comment,
                    "upvote",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    post.numComments.toLong().shortenNumber(),
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}