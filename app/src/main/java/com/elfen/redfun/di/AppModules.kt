package com.elfen.redfun.di

import com.elfen.redfun.data.remote.APIService
import com.elfen.redfun.data.remote.CommentDeserializer
import com.elfen.redfun.data.remote.PostDetailsDeserializer
import com.elfen.redfun.data.remote.models.RemoteComment
import com.elfen.redfun.data.remote.models.PostDetails
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    fun provideApiService(): APIService {
        val logging = HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        var gson = GsonBuilder()
            .registerTypeAdapter(RemoteComment::class.java, CommentDeserializer())
            .create()

        gson = gson
            .newBuilder()
            .registerTypeAdapter(
                PostDetails::class.javaObjectType,
                PostDetailsDeserializer(gson)
            )
            .create()

        return Retrofit.Builder()
            .baseUrl("https://api.reddit.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(APIService::class.java)
    }

}