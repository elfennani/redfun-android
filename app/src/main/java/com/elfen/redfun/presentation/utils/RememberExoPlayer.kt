package com.elfen.redfun.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

/**
 * A function to create an ExoPlayer instance meant for getting
 * access to a player quickly. It's better to create it in a ViewModel
 * then pass as property.
 */
@Composable
fun rememberExoPlayer(
    source: String?,
    isMuted: Boolean = false,
    autoPlay: Boolean = false,
): ExoPlayer {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            volume = if (isMuted) 0f else 1f
            if (source != null)
                setMediaItem(MediaItem.fromUri(source))
            prepare()
            playWhenReady = autoPlay
        }
    }

    LaunchedEffect(key1 = source) {
        if (source != null) {
            player.setMediaItem(MediaItem.fromUri(source))
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