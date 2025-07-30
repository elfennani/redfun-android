package com.elfen.redfun.ui.utils

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

class PlayerTimelineState(private val player: Player, private val delayMillis: Long = 1000L) {
    var currentPosition by mutableLongStateOf(0L)
        private set
    var duration by mutableLongStateOf(0L)
        private set
    var bufferedPosition by mutableLongStateOf(0L)
        private set

    var isMuted: Boolean by mutableStateOf(true)
        private set

    @OptIn(UnstableApi::class)
    suspend fun observe() {
        currentPosition = player.currentPosition
        duration = player.duration
        bufferedPosition = player.bufferedPosition

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    currentPosition = player.currentPosition
                    duration = player.duration
                    bufferedPosition = player.bufferedPosition
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    currentPosition = player.currentPosition
                    duration = player.duration
                    bufferedPosition = player.bufferedPosition
                }
            }
        })

        // Observe changes in the player's position every second
        while (true) {
            kotlinx.coroutines.delay(delayMillis)
            currentPosition = player.currentPosition
            duration = player.duration
            bufferedPosition = player.bufferedPosition
            isMuted = player.volume == 0f
        }
    }
}

@Composable
fun rememberPlayerTimelineState(
    player: Player,
    delayMillis: Long = 1000L
): PlayerTimelineState {
    val progressState = remember { PlayerTimelineState(player, delayMillis) }
    LaunchedEffect(player) {
        progressState.observe()
    }
    return progressState
}