package com.elfen.redfun.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.elfen.redfun.data.local.models.FeedPostEntity
import com.elfen.redfun.data.local.models.PostEntity
import com.elfen.redfun.data.local.models.PostMediaEntity
import com.elfen.redfun.data.local.relations.FeedWithPost
import com.elfen.redfun.data.local.relations.PostWithMedia

@Dao
interface PostDao {
    @Upsert
    suspend fun insertPost(post: PostEntity)

    @Upsert
    suspend fun insertPost(post: List<PostEntity>)

    @Upsert
    suspend fun insertMedia(media: List<PostMediaEntity>)

    @Query("SELECT * FROM post WHERE id = :id")
    suspend fun getPost(id: String): PostEntity?

    @Query("SELECT * FROM post WHERE id = :id")
    suspend fun getPostWithMedia(id: String): PostWithMedia?

    @Query("SELECT * FROM post_media WHERE postId = :postId")
    suspend fun getMedia(postId: String): List<PostMediaEntity>

    @Upsert
    suspend fun insertFeedPost(feedPost: FeedPostEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeedPost(feedPost: List<FeedPostEntity>)

    @Query("SELECT * FROM feed_post WHERE feed = :feed")
    suspend fun getFeedPosts(feed: String): List<FeedWithPost>

    @Query("SELECT * FROM feed_post WHERE feed = :feed ORDER BY created ASC")
    fun getPagingFeedPosts(feed: String): PagingSource<Int, FeedWithPost>

    @Query("DELETE FROM feed_post WHERE feed = :feed")
    suspend fun deleteByFeed(feed: String)

    @Query("DELETE FROM post_media")
    suspend fun deleteAllMedia()

    @Query("DELETE FROM feed_post")
    suspend fun deleteAllFeedPosts()

    @Query("DELETE FROM post")
    suspend fun deleteAllPosts()

    @Transaction
    suspend fun deleteAll() {
        deleteAllMedia()
        deleteAllFeedPosts()
        deleteAllPosts()
    }
}