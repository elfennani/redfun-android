@file:OptIn(ExperimentalPagingApi::class)

package com.elfen.redfun.data

import android.util.Log
import androidx.compose.ui.util.fastMapNotNull
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
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.data.remote.models.asDomainModel
import com.elfen.redfun.domain.models.Feed
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.domain.models.getTimeParameter
import com.elfen.redfun.domain.models.name
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val TAG = "FeedMediator"

class FeedMediator(
    val apiService: AuthAPIService,
    val database: Database,
    val sessionRepo: SessionRepo,
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
            }
        } catch (e: Exception){
            Log.e(TAG, "Error loading feed: ${e.message}", e)
            return MediatorResult.Error(e)
        }


        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                database.postDao().deleteByFeed(feed.name())
            }

            val posts = response.data.children.map { it.data.asDomainModel() }
            database.postDao().insertPost(posts.map { post ->
                PostEntity(
                    id = post.id,
                    body = post.body,
                    subreddit = post.subreddit,
                    score = post.score,
                    numComments = post.numComments,
                    author = post.author,
                    created = post.created.epochSeconds,
                    thumbnail = post.thumbnail,
                    url = post.url,
                    title = post.title,
                    nsfw = post.nsfw,
                    link = post.link,
                    subredditIcon = post.subredditIcon
                )
            })
            val media = posts.filter { (it.images?.size ?: 0) > 0 }.flatMap { post ->
                post.images!!.map { image ->
                    PostMediaEntity(
                        id = image.id,
                        postId = post.id,
                        source = image.source,
                        width = image.width,
                        height = image.height,
                        isVideo = false,
                        duration = null,
                        isGif = null,
                        fallback = null,
                    )
                }
            }

            val videos = posts.filter { it.video != null }.map {
                PostMediaEntity(
                    id = it.video!!.source,
                    postId = it.id,
                    source = it.video.source,
                    width = it.video.width,
                    height = it.video.height,
                    isVideo = true,
                    duration = it.video.duration,
                    isGif = it.video.isGif,
                    fallback = it.video.fallback
                )
            }

            database.postDao().insertMedia(videos)
            database.postDao().insertMedia(media)
            database.postDao().insertFeedPost(posts.mapIndexed { index,post ->
                FeedPostEntity(
                    feed = feed.name(),
                    postId = post.id,
                    created = Clock.System.now().toEpochMilliseconds(),
                    cursor = response.data.after,
                    index = index,
                )
            })
        }

        return MediatorResult.Success(
            endOfPaginationReached = response.data.after == null
        )
    }
}