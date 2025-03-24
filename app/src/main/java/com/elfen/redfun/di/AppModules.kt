package com.elfen.redfun.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.Room
import com.elfen.redfun.data.local.Database
import com.elfen.redfun.data.local.dao.SessionDao
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.data.remote.PublicAPIService
import com.elfen.redfun.data.remote.CommentDeserializer
import com.elfen.redfun.data.remote.PostDetailsDeserializer
import com.elfen.redfun.data.remote.models.RemoteComment
import com.elfen.redfun.data.remote.models.PostDetails
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    fun providePublicApiService(): PublicAPIService {
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
            .create(PublicAPIService::class.java)
    }

    @Provides
    fun provideAuthApiService(sessionDao: SessionDao, dataStore: DataStore<Preferences>): AuthAPIService{
        val logging = HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(object : Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val sessionId = runBlocking { dataStore.data.first()[stringPreferencesKey("session_id")] }

                    if(sessionId != null){
                        val session = runBlocking { sessionDao.getSession(sessionId) }
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer ${session!!.token}")
                            .build()
                        return chain.proceed(request)
                    }
                    return chain.proceed(chain.request())
                }
            })
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
            .baseUrl("https://oauth.reddit.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(AuthAPIService::class.java)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) = context.dataStore

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        val db = Room.databaseBuilder(context, Database::class.java, "database").build()
        return db
    }

    @Provides
    fun provideSessionDao(db: Database) = db.sessionDao()
}