package com.elfen.redfun.data.remote

import com.elfen.redfun.data.remote.models.AccessToken
import com.elfen.redfun.data.remote.models.DataCollection
import com.elfen.redfun.data.remote.models.Link
import com.elfen.redfun.data.remote.models.Listing
import com.elfen.redfun.data.remote.models.PostDetails
import com.elfen.redfun.data.remote.models.Profile
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PublicAPIService {
    @GET("/{feed}?raw_json=1")
    suspend fun getPosts(@Path("feed") feed: String = "best"): DataCollection<Listing<DataCollection<Link>>>

    @GET("/comments/{id}?threaded=0&showmedia=1&raw_json=1")
    suspend fun getComments(@Path("id") id: String): PostDetails

    @POST("https://www.reddit.com/api/v1/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Header("Authorization") auth: String
    ): AccessToken

    @POST("https://www.reddit.com/api/v1/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String,
        @Header("Authorization") auth: String
    ): AccessToken

    @GET("https://oauth.reddit.com/api/v1/me")
    suspend fun getUserProfile(@Header("Authorization") auth: String): Profile
}