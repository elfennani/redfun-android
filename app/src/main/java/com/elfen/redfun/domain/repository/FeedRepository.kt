package com.elfen.redfun.domain.repository

import androidx.paging.PagingData
import com.elfen.redfun.data.local.relations.FeedWithPost
import com.elfen.redfun.domain.model.Comment
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.domain.model.Sorting
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    val sortingFlow: Flow<Sorting?>

    fun getFeedPaging(feed: Feed): Flow<PagingData<FeedWithPost>>
    fun getPostWithComments(postId: String): Flow<Pair<Post, List<Comment>?>>

    suspend fun setSorting(sorting: Sorting)
}