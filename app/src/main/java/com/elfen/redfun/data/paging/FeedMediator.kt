@file:OptIn(ExperimentalPagingApi::class)

package com.elfen.redfun.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.elfen.redfun.data.local.Database
import com.elfen.redfun.data.local.models.FeedPostEntity
import com.elfen.redfun.data.local.models.PostEntity
import com.elfen.redfun.data.local.models.PostMediaEntity
import com.elfen.redfun.data.local.relations.FeedWithPost
import com.elfen.redfun.data.mappers.asDomainModel
import com.elfen.redfun.data.mappers.asEntity
import com.elfen.redfun.data.mappers.asFeedEntity
import com.elfen.redfun.data.mappers.asVideoEntity
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.getTimeParameter
import com.elfen.redfun.domain.model.name
import com.elfen.redfun.domain.repository.SessionRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "FeedMediator"

class FeedMediator(
    val apiService: AuthAPIService,
    val database: Database,
    val sessionRepo: SessionRepository,
    val feed: Feed,
) : RemoteMediator<Int, FeedWithPost>() {

    @OptIn(ExperimentalTime::class)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedWithPost>
    ): MediatorResult {
        val loadKey = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND ->
                return MediatorResult.Success(endOfPaginationReached = true)

            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                lastItem.feed.cursor
            }
        }

        val response = try {
            when (feed) {
                is Feed.Home -> apiService.getPosts(
                    feed.sorting.feed,
                    loadKey,
                    feed.sorting.getTimeParameter()
                )

                is Feed.SavedPosts -> {
                    val session = sessionRepo.getCurrentSession()

                    if (session == null) {
                        return MediatorResult.Error(
                            Throwable("No active session found")
                        )
                    }
                    apiService.getSavedPosts(session.username, loadKey)
                }

                is Feed.Subreddit -> apiService.getSubredditPosts(
                    feed.subreddit,
                    feed.sorting.feed,
                    loadKey,
                    feed.sorting.getTimeParameter()
                )

                is Feed.Search -> {
                    if (feed.subreddit != null)
                        apiService.getSubredditPostsByQuery(
                            feed.subreddit,
                            feed.query,
                            loadKey,
                            feed.sorting.feed,
                            feed.sorting.getTimeParameter(),
                            restrictSr = true
                        )
                    else apiService.getPostsByQuery(
                        feed.query,
                        loadKey,
                        feed.sorting.feed,
                        feed.sorting.getTimeParameter()
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading feed: ${e.message}", e)
            return MediatorResult.Error(e)
        }


        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                database.postDao().deleteByFeed(feed.name())
            }

            val posts = response.data.children.map { it.data.asDomainModel() }
            database.postDao().insertPost(posts.map { post ->
                post.asEntity()
            })
            val media = posts.filter { (it.images?.size ?: 0) > 0 }.flatMapIndexed { index, post ->
                post.images!!.map { image ->
                    image.asEntity(post.id, index)
                }
            }

            val videos = posts.filter { it.video != null }.map {
                it.asVideoEntity()
            }

            database.postDao().insertMedia(videos)
            database.postDao().insertMedia(media)
            database.postDao().insertFeedPost(posts.mapIndexed { index, post ->
                post.asFeedEntity(feed, response.data.after, index)
            })
        }

        return MediatorResult.Success(
            endOfPaginationReached = response.data.after == null
        )
    }
}