package com.elfen.redfun.data.remote

import com.elfen.redfun.data.remote.models.AccessToken
import com.elfen.redfun.data.remote.models.DataCollection
import com.elfen.redfun.data.remote.models.Link
import com.elfen.redfun.data.remote.models.Listing
import com.elfen.redfun.data.remote.models.PostDetails
import com.elfen.redfun.data.remote.models.Profile
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthAPIService {
    @GET("/{feed}?raw_json=1&limit=25")
    suspend fun getPosts(@Path("feed") feed: String = "best", @Query("after") after: String? = null, @Query("t") time: String? = null): DataCollection<Listing<DataCollection<Link>>>

    @GET("/comments/{id}?threaded=0&showmedia=1&raw_json=1")
    suspend fun getComments(@Path("id") id: String): PostDetails

    @GET("/api/v1/me")
    suspend fun getUserProfile(): Profile
}