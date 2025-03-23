package com.elfen.redfun.data

import android.util.Log
import com.elfen.redfun.data.remote.APIService
import com.elfen.redfun.data.remote.models.asDomainModel
import com.elfen.redfun.domain.models.Comment
import com.elfen.redfun.domain.models.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FeedService"

@Singleton
class FeedService @Inject constructor(private val apiService: APIService) {
    private val cachedPosts: MutableMap<String, List<Post>> = mutableMapOf()

    suspend fun getPosts(feed: String): List<Post> {
        if (cachedPosts[feed] == null) {
            cachedPosts[feed] =
                apiService.getPosts(feed).data.children.map { it.data.asDomainModel() }
        }

        return cachedPosts[feed]!!
    }

    fun getPostWithComments(id: String): Flow<Pair<Post, List<Comment>?>> = flow{
        val cached = cachedPosts.values.flatten().find {
            Log.d(TAG, "getPostWithComments: ${it.id} === $id = ${it.id == id}")
            it.id == id
        }
        if(cached != null){
            emit(Pair(cached, null))
        }

        val response = apiService.getComments(id)
        val post = response.post.asDomainModel()
        val comments = response.comments.map { it.asDomainModel() }

        emit(Pair(post, comments))
    }
}
