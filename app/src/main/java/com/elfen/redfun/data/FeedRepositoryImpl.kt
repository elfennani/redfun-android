package com.elfen.redfun.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.elfen.redfun.data.local.Database
import com.elfen.redfun.data.local.dao.FeedCursorDao
import com.elfen.redfun.data.local.dao.SortingDao
import com.elfen.redfun.data.local.models.toDomain
import com.elfen.redfun.data.local.models.toEntity
import com.elfen.redfun.data.mappers.asDomainModel
import com.elfen.redfun.data.paging.FeedMediator
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.domain.model.Comment
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.name
import com.elfen.redfun.domain.repository.FeedRepository
import com.elfen.redfun.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val apiService: AuthAPIService,
    private val sessionRepo: SessionRepository,
    private val sortingDao: SortingDao,
    private val database: Database
) : FeedRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getFeedPaging(feed: Feed) = Pager(
        config = PagingConfig(
            pageSize = 25,
            prefetchDistance = 12,
            initialLoadSize = 25
        ),
        remoteMediator = FeedMediator(apiService, database, sessionRepo, feed)
    ) {
        database.postDao().getPagingFeedPosts(feed.name())
    }.flow

    override val sortingFlow: Flow<Sorting?>
        get() = sortingDao.allSortingFlow().map {
            val session = sessionRepo.getCurrentSession()!!
            it.find { it.userId == session.userId }?.toDomain()
        }

    override suspend fun setSorting(sorting: Sorting) {
        val session = sessionRepo.getCurrentSession()!!
        sortingDao.upsert(sorting.toEntity(session.userId))
    }

    override fun getPostWithComments(id: String): Flow<Pair<Post, List<Comment>?>> = flow {
        val cached = database.postDao().getPostWithMedia(id)?.asDomainModel()
        if (cached != null) {
            emit(Pair(cached, null))
        }

        val response = apiService.getComments(id)
        val post = response.post.asDomainModel()
        val comments = response.comments.map { it.asDomainModel() }

        emit(Pair(post, comments))
    }
}
