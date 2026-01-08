package com.elfen.redfun.presentation.screens.details

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.toRoute
import com.elfen.redfun.domain.repository.FeedRepository
import com.elfen.redfun.domain.usecase.DownloadPostUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val feedRepositoryImpl: FeedRepository,
    private val downloadPostUseCase: DownloadPostUseCase
) : ViewModel() {
    private val id = savedStateHandle.toRoute<PostDetailRoute>().id
    lateinit var exoPlayer: ExoPlayer

    val state = feedRepositoryImpl.getPostWithComments(id).map {
        PostDetailUiState(
            post = it.first,
            comments = it.second,
            isLoading = it.second === null
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PostDetailUiState(null, null, true)
    )

    init {
        exoPlayer = ExoPlayer
            .Builder(context)
            .build()
            .apply {
                playWhenReady = true
                volume = 0f
            }

        viewModelScope.launch {
            state.first{
                it.post != null
            }.let {
                if(it.post?.video?.source != null) {
                    exoPlayer.setMediaItem(MediaItem.fromUri(it.post.video.source))
                    exoPlayer.prepare()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    fun downloadPost() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadPostUseCase(id)
        }
    }
}