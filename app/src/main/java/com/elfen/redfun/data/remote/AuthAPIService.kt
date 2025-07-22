package com.elfen.redfun.data.remote

import com.elfen.redfun.data.remote.models.DataCollection
import com.elfen.redfun.data.remote.models.Link
import com.elfen.redfun.data.remote.models.Listing
import com.elfen.redfun.data.remote.models.PostDetails
import com.elfen.redfun.data.remote.models.RemoteProfile
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthAPIService {
    @GET("/{feed}?raw_json=1&limit=25&sr_detail=1")
    suspend fun getPosts(@Path("feed") feed: String = "best", @Query("after") after: String? = null, @Query("t") time: String? = null): DataCollection<Listing<DataCollection<Link>>>

    @GET("/comments/{id}?threaded=0&showmedia=1&raw_json=1&sr_detail=1")
    suspend fun getComments(@Path("id") id: String): PostDetails

    @GET("/api/v1/me?raw_json=1")
    suspend fun getUserProfile(): RemoteProfile

    @GET("/user/{username}/saved?raw_json=1&type=links&limit=25&sr_detail=1")
    suspend fun getSavedPosts(
        @Path("username") username: String,
        @Query("after") after: String? = null
    ): DataCollection<Listing<DataCollection<Link>>>

    @GET("/r/{subreddit}/{feed}?raw_json=1&limit=25&sr_detail=1")
    suspend fun getSubredditPosts(
        @Path("subreddit") subreddit: String,
        @Path("feed") feed: String = "hot",
        @Query("after") after: String? = null,
        @Query("t") time: String? = null
    ): DataCollection<Listing<DataCollection<Link>>>
}