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
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.domain.models.getTimeParameter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val TAG = "FeedMediator"

class FeedMediator(
    val apiService: AuthAPIService,
    val database: Database,
    val sorting: Sorting,
) : RemoteMediator<Int, FeedWithPost>() {

    @OptIn(ExperimentalTime::class)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedWithPost>
    ): MediatorResult {
        val loadKey = when (loadType) {
            LoadType.REFRESH -> null
            // In this example, you never need to prepend, since REFRESH
            // will always load the first page in the list. Immediately
            // return, reporting end of pagination.
            LoadType.PREPEND ->
                return MediatorResult.Success(endOfPaginationReached = true)

            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()

                // You must explicitly check if the last item is null when
                // appending, since passing null to networkService is only
                // valid for initial load. If lastItem is null it means no
                // items were loaded after the initial REFRESH and there are
                // no more items to load.
                if (lastItem == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                lastItem.feed.cursor
            }
        }

        Log.d(TAG, "load: ${sorting.feed}");
        Log.d(TAG, "load: $loadType $loadKey")

        // Suspending network load via Retrofit. This doesn't need to be
        // wrapped in a withContext(Dispatcher.IO) { ... } block since
        // Retrofit's Coroutine CallAdapter dispatches on a worker
        // thread.
        val response =
            apiService.getPosts(sorting.feed, loadKey, sorting.getTimeParameter())


        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                database.postDao().deleteByFeed(sorting.feed)
            }

            // Insert new users into database, which invalidates the
            // current PagingData, allowing Paging to present the updates
            // in the DB.
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
                    link = post.link
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
            database.postDao().insertFeedPost(posts.map { post ->
                FeedPostEntity(
                    feed = sorting.feed,
                    postId = post.id,
                    created = Clock.System.now().toEpochMilliseconds(),
                    cursor = response.data.after
                )
            })
        }

        return MediatorResult.Success(
            endOfPaginationReached = response.data.after == null
        )
    }
}