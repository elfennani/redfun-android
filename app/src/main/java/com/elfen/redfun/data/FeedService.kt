package com.elfen.redfun.data

import android.util.Log
import com.elfen.redfun.data.local.dao.FeedCursorDao
import com.elfen.redfun.data.local.models.FeedCursorEntity
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.data.remote.PublicAPIService
import com.elfen.redfun.data.remote.models.asDomainModel
import com.elfen.redfun.domain.models.Comment
import com.elfen.redfun.domain.models.Post
import com.elfen.redfun.ui.utils.Resource
import com.elfen.redfun.ui.utils.resourceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FeedService"

@Singleton
class FeedService @Inject constructor(private val apiService: AuthAPIService, private val feedCursorDao: FeedCursorDao, private val sessionRepo: SessionRepo) {
    private val cachedPosts: MutableMap<String, List<Post>> = mutableMapOf()

    suspend fun getPosts(feed: String, isFetchingNextPage: Boolean = false): Resource<List<Post>> {
        return resourceOf {
            if (cachedPosts[feed] == null || isFetchingNextPage) {
                val session = sessionRepo.getCurrentSession()
                if (session == null) {
                    return@resourceOf emptyList()
                }

                val cursor = feedCursorDao.getCursor(feed, session.userId)

                val response = apiService.getPosts(feed, cursor)

                val posts = response.data.children.map { it.data.asDomainModel() }

                cachedPosts[feed] = (cachedPosts[feed] ?: emptyList()) + posts

                feedCursorDao.upsertCursor(
                    FeedCursorEntity(
                        feed = feed,
                        cursor = response.data.after ?: "",
                        sessionId = session.userId
                    )
                )

                if(isFetchingNextPage){
                    return@resourceOf posts
                }
            }

            return@resourceOf cachedPosts[feed]!!
        }
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
