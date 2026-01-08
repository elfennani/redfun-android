package com.elfen.redfun.presentation.utils

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

class PlayerState(private val player: Player, private val delayMillis: Long = 1000L) {
    var currentPosition by mutableLongStateOf(0L)
        private set
    var duration by mutableLongStateOf(0L)
        private set
    var bufferedPosition by mutableLongStateOf(0L)
        private set

    var isMuted: Boolean by mutableStateOf(true)
        private set

    var isPlaying: Boolean by mutableStateOf(false)
        private set

    var isBuffering: Boolean by mutableStateOf(false)
        private set

    var isSpeeding: Boolean by mutableStateOf(false)
        private set

    var isEnded: Boolean by mutableStateOf(false)
        private set

    @OptIn(UnstableApi::class)
    suspend fun observe() {
        currentPosition = player.currentPosition
        duration = if (player.duration == C.TIME_UNSET) -1 else player.duration
        bufferedPosition = player.bufferedPosition
        this@PlayerState.isPlaying = isPlaying
        isBuffering = player.playbackState == Player.STATE_BUFFERING
        isSpeeding = player.playbackParameters.speed == 2f
        isEnded = player.playbackState == Player.STATE_ENDED

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    currentPosition = player.currentPosition
                    duration = if (player.duration == C.TIME_UNSET) -1 else player.duration
                    bufferedPosition = player.bufferedPosition
                }

                this@PlayerState.isPlaying = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    currentPosition = player.currentPosition
                    duration = if (player.duration == C.TIME_UNSET) -1 else player.duration
                    bufferedPosition = player.bufferedPosition
                    this@PlayerState.isPlaying = isPlaying
                }

                isBuffering = playbackState == Player.STATE_BUFFERING
                isEnded = playbackState == Player.STATE_ENDED
            }

            override fun onVolumeChanged(volume: Float) {
                super.onVolumeChanged(volume)
                isMuted = volume == 0f
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                super.onPlaybackParametersChanged(playbackParameters)
                isSpeeding = playbackParameters.speed == 2f
            }
        })

        // Observe changes in the player's position every second
        while (true) {
            kotlinx.coroutines.delay((delayMillis / player.playbackParameters.speed).toLong())
            currentPosition = player.currentPosition
            duration = if (player.duration == C.TIME_UNSET) -1 else player.duration
            bufferedPosition = player.bufferedPosition
            isMuted = player.volume == 0f
            isSpeeding = player.playbackParameters.speed == 2f
            this@PlayerState.isPlaying = isPlaying
        }
    }
}

@Composable
fun rememberPlayerState(
    player: Player,
    delayMillis: Long = 1000L
): PlayerState {
    val progressState = remember { PlayerState(player, delayMillis) }
    LaunchedEffect(player) {
        progressState.observe()
    }
    return progressState
}