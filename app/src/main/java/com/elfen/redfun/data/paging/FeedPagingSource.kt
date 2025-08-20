package com.elfen.redfun.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.elfen.redfun.data.local.dao.FeedCursorDao
import com.elfen.redfun.data.local.models.FeedCursorEntity
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.data.remote.models.asDomainModel
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.domain.model.ResourceError
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.getTimeParameter
import com.elfen.redfun.domain.repository.SessionRepository
import com.elfen.redfun.presentation.utils.toResource

class FeedPagingSource(
    val apiService: AuthAPIService,
    val sessionRepo: SessionRepository,
    val feedCursorDao: FeedCursorDao,
    val sorting: Sorting
) : PagingSource<String, Post>() {
    override fun getRefreshKey(state: PagingState<String, Post>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.nextKey
        }
    }

    suspend fun getCursor(): String? {
        val session = sessionRepo.getCurrentSession()
        if (session == null) {
            return null
        }

        if (!sorting.shouldSaveCursor) {
            feedCursorDao.deleteCursor(sorting.feed, session.userId);
        }

        val cursor = feedCursorDao.getCursor(sorting.feed, session.userId)
        return cursor
    }

    suspend fun setCursor(cursor: String){
        val session = sessionRepo.getCurrentSession()
        if (session == null) {
            return
        }
        feedCursorDao.upsertCursor(
            FeedCursorEntity(
                feed = sorting.feed,
                cursor = cursor,
                sessionId = session.userId
            )
        )
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        try {
            val nextPageNumber = params.key ?: getCursor();
            val response =
                apiService.getPosts(sorting.feed, nextPageNumber, sorting.getTimeParameter())
            if(response.data.after != null)
                setCursor(response.data.after)
            return LoadResult.Page(
                data = response.data.children.map { it.data.asDomainModel() },
                prevKey = null, // Only paging forward.
                nextKey = response.data.after
            )
        } catch (e: Exception) {
            return LoadResult.Error(ResourceError(e.toResource()))
        }
    }
}