package com.elfen.redfun.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.elfen.redfun.data.SettingsRepositoryImpl
import com.elfen.redfun.data.local.Database
import com.elfen.redfun.data.local.dao.SessionDao
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.data.remote.PublicAPIService
import com.elfen.redfun.data.remote.serializers.CommentDeserializer
import com.elfen.redfun.data.remote.serializers.PostDetailsDeserializer
import com.elfen.redfun.data.remote.models.RemoteComment
import com.elfen.redfun.data.remote.models.PostDetails
import com.elfen.redfun.domain.repository.SessionRepository
import com.elfen.redfun.domain.repository.SettingsRepository
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val TAG = "AppModules"

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

    @OptIn(ExperimentalTime::class)
    @Provides
    fun provideAuthApiService(sessionDao: SessionDao, sessionRepo: SessionRepository): AuthAPIService {
        val logging = HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val session = runBlocking { sessionRepo.getCurrentSession() }

                    if (session != null) {
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer ${session.token}")

                        if (kotlin.time.Clock.System.now() >= Instant.fromEpochSeconds(session.expiresAt)) {
                            Log.d(TAG, "REFRESHING TOKEN!")
                            val newSession =
                                runBlocking { sessionRepo.refreshSession(session.userId); }
                            request.removeHeader("Authorization");
                            request.addHeader("Authorization", "Bearer ${newSession.token}");
                        }

                        return chain.proceed(request.build())
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
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        val db = Room.databaseBuilder(context, Database::class.java, "database").build()
        return db
    }

    @Provides
    @Singleton
    fun provideSessionDao(db: Database) = db.sessionDao()

    @Provides
    @Singleton
    fun provideFeedCursorDao(db: Database) = db.feedCursorDao()

    @Provides
    @Singleton
    fun provideSortingDao(db: Database) = db.sortingDao()

    @Provides
    @Singleton
    fun providePostDao(db: Database) = db.postDao()

    @Provides
    @Singleton
    fun provideProfileDao(db: Database) = db.profileDao()
}