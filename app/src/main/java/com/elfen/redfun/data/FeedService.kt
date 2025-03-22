package com.elfen.redfun.data

import android.util.Log
import androidx.compose.runtime.MutableState
import com.elfen.redfun.data.remote.APIService
import com.elfen.redfun.data.remote.models.Link
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FeedService"

@Singleton
class FeedService @Inject constructor(private val apiService: APIService) {
    private val cachedPosts: MutableMap<String, List<Link>> = mutableMapOf()

    suspend fun getPosts(feed: String): List<Link> {
        if (cachedPosts[feed] == null) {
            cachedPosts[feed] = apiService.getPosts(feed).data.children.map { it.data }
        }

        return cachedPosts[feed]!!
    }
}
