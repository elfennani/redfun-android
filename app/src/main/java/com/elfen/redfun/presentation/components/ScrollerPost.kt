package com.elfen.redfun.presentation.components

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.elfen.redfun.domain.models.Post
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.presentation.utils.rememberPlayerTimelineState
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(
    ExperimentalTime::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ScrollerPost(
    modifier: Modifier = Modifier,
    post: Post,
    showSubreddit: Boolean = true,
    onPostClick: () -> Unit,
    onClickSubreddit: () -> Unit = {},
    shouldMute: Boolean = true
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val exoPlayer = rememberExoPlayer(post, muted = shouldMute)
    val timelineState = rememberPlayerTimelineState(player = exoPlayer, 250)

    LaunchedEffect(exoPlayer) {
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        PostContent(
            post = post,
            player = exoPlayer,
            autoPlay = true,
            onClick = {
                onPostClick()
            },
            isScroller = true
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.5f to Color.Black.copy(alpha = 0.5f),
                                1f to Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(end = 80.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onPostClick()
                    }
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (timelineState.duration > 0L) {
                    IconButton(onClick = {
                        exoPlayer.volume = if (timelineState.isMuted) 1f else 0f
                        coroutineScope.launch {
                            context.dataStore.edit {
                                it[booleanPreferencesKey("shouldMute")] = !timelineState.isMuted
                            }
                        }
                    }, modifier = Modifier.offset(x = (-12).dp)) {
                        Icon(
                            if (timelineState.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            null
                        )
                    }
                }
                if ((post.images?.size ?: 0) > 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { onPostClick() }
                    ) {
                        Icon(
                            painterResource(R.drawable.baseline_photo_library_24),
                            null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Gallery (${post.images?.size ?: 0})",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.wrapContentWidth()
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onClickSubreddit() }
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
                        verticalArrangement = Arrangement.spacedBy(
                            (-2).dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (showSubreddit) {
                            Text(
                                "r/${post.subreddit}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                maxLines = 1,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Text(
                                "u/${post.author} · ${formatDistanceToNowStrict(post.created)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                            )
                        }
                    }
                }
                Text(
                    post.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )

                if (timelineState.duration > 0L) {
                    val rawPercentage by remember {
                        derivedStateOf {
                            (timelineState.currentPosition.toFloat() / timelineState.duration.toFloat())
                        }
                    }
                    val animatedPercentage by animateFloatAsState(
                        targetValue = rawPercentage,
                        label = "timelinePercentage",
                        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
                    )
                    var sliderPosition by remember { mutableFloatStateOf(animatedPercentage) }
                    var isUserSeeking by remember { mutableStateOf(false) }
                    // Keep sliderPosition in sync with animatedPercentage when not seeking
                    if (!isUserSeeking) {
                        sliderPosition = animatedPercentage
                    }
                    Spacer(Modifier.height(4.dp))
                    Slider(
                        value = sliderPosition,
                        onValueChange = { value ->
                            isUserSeeking = true
                            sliderPosition = value
                        },
                        onValueChangeFinished = {
                            exoPlayer.seekTo((sliderPosition * timelineState.duration).toLong())
                            isUserSeeking = false
                        },
                        modifier = Modifier.height(12.dp),
                    )
                }
            }


        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun ScrollerPostPreview() {
    val samplePost = Post(
        id = "1",
        body = "This is a sample post body.",
        subreddit = "Android",
        subredditIcon = null,
        score = 123,
        numComments = 45,
        author = "sampleUser",
        created = Instant.fromEpochMilliseconds(1633072800000), // Example timestamp
        thumbnail = null,
        url = "https://www.example.com",
        title = "Sample Post Title",
        nsfw = false,
        link = null,
        images = null,
        video = null
    )
    ScrollerPost(post = samplePost, onPostClick = {})
}
