package com.elfen.redfun.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeMute
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.elfen.redfun.presentation.theme.AppTheme
import com.elfen.redfun.presentation.utils.PlayerState
import com.elfen.redfun.presentation.utils.rememberExoPlayer
import com.elfen.redfun.presentation.utils.rememberPlayerState
import kotlinx.coroutines.delay

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    source: String,
    isMuted: Boolean = false,
    onPlayerStateUpdate: (PlayerState) -> Unit = {},
    backdropColor: Color = Color.Black,
    thumbnail: String? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    showDuration: Boolean = true
) {
    val exoPlayer = rememberExoPlayer(source, isMuted, autoPlay = true)
    val playerState = rememberPlayerState(player = exoPlayer)

    LaunchedEffect(playerState) {
        onPlayerStateUpdate(playerState)
    }

    VideoPlayer(
        modifier,
        exoPlayer,
        playerState,
        backdropColor,
        thumbnail,
        shape,
        showDuration
    )
}

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    playerState: PlayerState = rememberPlayerState(player = exoPlayer),
    backdropColor: Color = Color.Black,
    thumbnail: String? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    showDuration: Boolean = true
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var overlayVisible by remember { mutableStateOf(false) }

    var hasSeekedForwards by remember { mutableStateOf(false) }
    var hasSeekedBackwards by remember { mutableStateOf(false) }
    var showThumbnail by remember { mutableStateOf(true) }

    /**
     * Three state:
     * - "0" means nothing happened
     * - "1" means *Paused*
     * - "2" means *Resumed*
     */
    var hasTogglePlayback by remember { mutableIntStateOf(0) }

    if (thumbnail != null)
        DisposableEffect(key1 = exoPlayer) {
            val listener = object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    super.onRenderedFirstFrame()
                    showThumbnail = false
                }
            }
            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
            }
        }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backdropColor, shape)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        overlayVisible = !overlayVisible
                    },
                    onLongPress = {
                        exoPlayer.setPlaybackSpeed(2f)
                    },
                    onPress = {
                        awaitRelease()
                        exoPlayer.setPlaybackSpeed(1f)
                    },
                    onDoubleTap = { offset ->
                        val threshold = size.width * 0.33f
                        val isGoingBack = offset.x < threshold
                        val isGoingForward = (size.width - offset.x) < threshold

                        if (isGoingBack) {
                            exoPlayer.seekBack()
                            hasSeekedBackwards = true
                        } else if (isGoingForward) {
                            exoPlayer.seekForward()
                            hasSeekedForwards = true
                        } else {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                                hasTogglePlayback = 1
                            } else {
                                if (exoPlayer.playbackState == Player.STATE_ENDED)
                                    exoPlayer.seekTo(0)

                                exoPlayer.play()
                                hasTogglePlayback = 2
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    this.player = exoPlayer
                    this.useController = false
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
            }
        )

        if (showThumbnail && thumbnail != null)
            AsyncImage(
                model = thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize(),
                contentScale = ContentScale.Crop
            )

        val animationSpec: FiniteAnimationSpec<Float> = spring(
            dampingRatio = 0.5f,
            stiffness = 100f
        )
        val slideAnimationSpec: FiniteAnimationSpec<IntOffset> = tween()

        AnimatedVisibility(
            visible = overlayVisible || playerState.isBuffering || playerState.isEnded,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            enter = fadeIn(animationSpec) + slideIn(animationSpec = slideAnimationSpec) { intSize ->
                IntOffset(0, intSize.height)
            },
            exit = fadeOut(animationSpec) + slideOut(slideAnimationSpec) { intSize ->
                IntOffset(0, intSize.height)
            }
        ) {
            LaunchedEffect(overlayVisible) {
                if (overlayVisible) {
                    delay(5000)
                    overlayVisible = false
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(0f),
                                Color.Black.copy(0.5f)
                            )
                        )
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (playerState.isPlaying)
                    IconButton(
                        onClick = {
                            exoPlayer.pause()
                        }
                    ) {
                        Icon(Icons.Rounded.Pause, "Pause", tint = Color.White)
                    }
                else
                    IconButton(
                        onClick = {
                            if (playerState.isEnded)
                                exoPlayer.seekTo(0)
                            exoPlayer.play()
                        }
                    ) {
                        if (playerState.isBuffering)
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        else
                            Icon(
                                if (playerState.isEnded) Icons.Rounded.Replay else Icons.Rounded.PlayArrow,
                                "Play",
                                tint = Color.White
                            )
                    }

                Text(
                    if (showDuration)
                        "${playerState.currentPosition.asTime()} / ${playerState.duration.asTime()}"
                    else
                        playerState.currentPosition.asTime(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {
                        exoPlayer.volume = if (exoPlayer.volume == 0f) 1f else 0f
                    }
                ) {
                    Icon(
                        imageVector =
                            if (playerState.isMuted)
                                Icons.AutoMirrored.Rounded.VolumeMute
                            else
                                Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Play",
                        tint = Color.White
                    )
                }
            }
        }

        VideoTooltip(
            visible = hasSeekedForwards,
            onRequestHide = { hasSeekedForwards = false }
        ) {
            Icon(Icons.Rounded.FastForward, null)
            Text("+15s")
        }

        VideoTooltip(
            visible = hasSeekedBackwards,
            onRequestHide = { hasSeekedBackwards = false }
        ) {
            Icon(Icons.Rounded.FastRewind, null)
            Text("-5s")
        }

        VideoTooltip(
            visible = hasTogglePlayback == 1,
            onRequestHide = { hasTogglePlayback = 0 }
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.Pause,
                contentDescription = null
            )
        }

        VideoTooltip(
            visible = hasTogglePlayback == 2,
            onRequestHide = { hasTogglePlayback = 0 }
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null
            )
        }

        VideoTooltip(
            visible = playerState.isSpeeding,
        ) {
            Icon(
                Icons.Rounded.FastForward,
                null
            )
            Text("x2")
        }
    }
}

@Composable
private fun VideoTooltip(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onRequestHide: () -> Unit = {},
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier
            .size(64.dp),
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 250)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 250)
        )
    ) {
        LaunchedEffect(visible) {
            if (visible) {
                delay(1000)
                onRequestHide()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.25f), CircleShape),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CompositionLocalProvider(
                LocalContentColor provides Color.White,
                LocalTextStyle provides MaterialTheme.typography.labelMedium
            ) {
                content()
            }
        }
    }
}

private fun Long.asTime(): String {
    if (this == -1L)
        return "00:00"

    val seconds = (this / 1000) % 60
    val minutes = (this / 1000).floorDiv(60)

    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun VideoPlayerPreview() {
    val source = "https://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4"

    AppTheme() {
        Scaffold() {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9),
                    source = source,
                    isMuted = true
                )
            }
        }
    }
}