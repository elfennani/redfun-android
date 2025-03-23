package com.elfen.redfun.data.remote

import com.elfen.redfun.data.remote.models.DataCollection
import com.elfen.redfun.data.remote.models.Link
import com.elfen.redfun.data.remote.models.Listing
import com.elfen.redfun.data.remote.models.PostDetails
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {
    @GET("/{feed}?raw_json=1")
    suspend fun getPosts(@Path("feed") feed: String = "best"): DataCollection<Listing<DataCollection<Link>>>

    @GET("/comments/{id}?threaded=0&showmedia=1&raw_json=1")
    suspend fun getComments(@Path("id") id: String): PostDetails
}