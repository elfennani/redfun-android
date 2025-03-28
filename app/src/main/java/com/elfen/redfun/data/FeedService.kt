package com.elfen.redfun.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.elfen.redfun.data.local.dao.FeedCursorDao
import com.elfen.redfun.data.local.dao.SortingDao
import com.elfen.redfun.data.local.models.FeedCursorEntity
import com.elfen.redfun.data.local.models.SortingEntity
import com.elfen.redfun.data.local.models.toDomain
import com.elfen.redfun.data.local.models.toEntity
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.data.remote.PublicAPIService
import com.elfen.redfun.data.remote.models.asDomainModel
import com.elfen.redfun.domain.models.Comment
import com.elfen.redfun.domain.models.Post
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.ui.utils.Resource
import com.elfen.redfun.ui.utils.resourceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FeedService"

@Singleton
class FeedService @Inject constructor(
    private val apiService: AuthAPIService,
    private val feedCursorDao: FeedCursorDao,
    private val sessionRepo: SessionRepo,
    private val sortingDao: SortingDao,
    private val dataStore: DataStore<Preferences>
) {
    private val cachedPosts: MutableMap<String, List<Post>> = mutableMapOf()

    fun getFeedPaging(sorting: Sorting) = Pager(PagingConfig(pageSize = 25)) {
        FeedPagingSource(
            apiService,
            sessionRepo,
            feedCursorDao,
            sorting
        )
    }.flow

    suspend fun getPosts(
        sorting: Sorting,
        isFetchingNextPage: Boolean = false
    ): Resource<List<Post>> {
        val feed = sorting.feed

        return resourceOf {
            if (cachedPosts[feed] == null || isFetchingNextPage) {
                val session = sessionRepo.getCurrentSession()
                if (session == null) {
                    return@resourceOf emptyList()
                }

                if (!sorting.shouldSaveCursor && !isFetchingNextPage) {
                    feedCursorDao.deleteCursor(feed, session.userId);
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

                if (isFetchingNextPage) {
                    return@resourceOf posts
                }
            }

            return@resourceOf cachedPosts[feed]!!
        }
    }

    suspend fun getSorting(): Resource<Sorting> {
        return resourceOf {
            val session = sessionRepo.getCurrentSession()

            if (session == null) {
                throw Exception("No session found")
            }

            val sorting = sortingDao.getSorting(session.userId)

            if (sorting != null) {
                return@resourceOf sorting.toDomain()
            }

            sortingDao.upsert(Sorting.Best.toEntity(session.userId))

            return@resourceOf Sorting.Best
        }
    }

    fun getSortingFlow() =
        sortingDao.allSortingFlow().map {
            val session = sessionRepo.getCurrentSession()!!
            it.find { it.userId == session.userId }?.toDomain()
        }

    suspend fun setSorting(sorting: Sorting) {
        val session = sessionRepo.getCurrentSession()!!
        sortingDao.upsert(sorting.toEntity(session.userId))
    }

    fun getPostWithComments(id: String): Flow<Pair<Post, List<Comment>?>> = flow {
        val cached = cachedPosts.values.flatten().find {
            Log.d(TAG, "getPostWithComments: ${it.id} === $id = ${it.id == id}")
            it.id == id
        }
        if (cached != null) {
            emit(Pair(cached, null))
        }

        val response = apiService.getComments(id)
        val post = response.post.asDomainModel()
        val comments = response.comments.map { it.asDomainModel() }

        emit(Pair(post, comments))
    }
}
