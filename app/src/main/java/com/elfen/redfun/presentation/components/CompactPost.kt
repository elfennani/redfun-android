@file:Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")

package com.elfen.redfun.presentation.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Duration
import kotlin.math.abs
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.data.rememberSettings
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.presentation.utils.isWifiNetwork
import com.elfen.redfun.presentation.utils.rememberExoPlayer
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant


@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.O)
fun formatDistanceToNowStrict(date: Instant): String {
    val now = Clock.System.now()
    val duration = Duration.between(date.toJavaInstant(), now.toJavaInstant())
    val seconds = abs(duration.seconds)

    return when {
        seconds < 60 -> "$seconds second${if (seconds != 1L) "s" else ""}"
        seconds < 3600 -> {
            val minutes = seconds / 60
            "$minutes minute${if (minutes != 1L) "s" else ""}"
        }

        seconds < 86400 -> {
            val hours = seconds / 3600
            "$hours hour${if (hours != 1L) "s" else ""}"
        }

        seconds < 2592000 -> {
            val days = seconds / 86400
            "$days day${if (days != 1L) "s" else ""}"
        }

        seconds < 31536000 -> {
            val months = seconds / 2592000
            "$months month${if (months != 1L) "s" else ""}"
        }

        else -> {
            val years = seconds / 31536000
            "$years year${if (years != 1L) "s" else ""}"
        }
    }
}

@SuppressLint("DefaultLocale")
fun shortenNumber(value: Long): String {
    return when {
        value >= 1_000_000_000 -> String.format("%.1fB", value / 1_000_000_000.0)
        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000.0)
        value >= 1_000 -> String.format("%.1fK", value / 1_000.0)
        else -> value.toString()
    }.replace(".0", "") // Remove trailing ".0" if not needed
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun CompactPost(
    modifier: Modifier = Modifier,
    post: Post,
    showSubreddit: Boolean = true,
    onClickSubreddit: () -> Unit,
    onClickUserProfile: () -> Unit,
    onClickPost: () -> Unit,
    navigateToFlair: (subreddit: String, flair: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val shape = RoundedCornerShape(12.dp)
    var detailBottomSheetVisible by remember { mutableStateOf(false) }
    val detailSheetState = rememberModalBottomSheetState(false)

    if (detailBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = detailSheetState,
            onDismissRequest = { detailBottomSheetVisible = false },
        ) {
            PostCard(
                post = post,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        onClickPost()
                        scope.launch {
                            detailSheetState.hide()
                        }.invokeOnCompletion {
                            detailBottomSheetVisible = false
                        }
                    },
                onNavigateSubreddit = onClickSubreddit,
                onNavigateUserProfile = onClickUserProfile,
                truncate = false,
                showSubreddit = true,
                navigateToFlair = {
                    navigateToFlair(post.subreddit, it)
                }
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .clickable {
                detailBottomSheetVisible = true
            }
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
                val player = rememberExoPlayer(source = post.video.source)

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

                VideoPlayer(
                    exoPlayer = player,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat()),
                    shape = shape,
                    showDuration = false,
                    thumbnail = post.thumbnail
                )
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

        if (post.images.isNullOrEmpty() && post.video == null) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f)
                ) {
                    if (showSubreddit) {
                        Text(
                            "r/${post.subreddit}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            maxLines = 1,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.25.sp
                        )
                    }

                    Text(
                        post.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                        lineHeight = 18.sp,
                        fontSize = 12.sp,
                    )
                }
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun Badge(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            .padding(vertical = 4.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
            LocalTextStyle provides TextStyle(fontSize = 10.sp)
        ) {
            content()
        }
    }
}