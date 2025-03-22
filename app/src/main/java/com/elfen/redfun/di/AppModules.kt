package com.elfen.redfun.di

import com.elfen.redfun.data.remote.APIService
import com.elfen.redfun.data.remote.models.Comment
import com.elfen.redfun.data.remote.models.DataCollection
import com.elfen.redfun.data.remote.models.Link
import com.elfen.redfun.data.remote.models.Listing
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

data class Post(
    val data: Link,
    val comments: List<Comment>
)

class CustomDeserializer(
    private val gson: Gson
) : JsonDeserializer<Post> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Post {
        val jsonArray = json.asJsonArray
        if (jsonArray.size() != 2) throw JsonParseException("Expected an array of size 2")

        val typeA = object : TypeToken<DataCollection<Listing<DataCollection<Link>>>>() {}.type
        val typeB = object : TypeToken<DataCollection<Listing<DataCollection<Comment>>>>() {}.type

        val first =
            gson.fromJson<DataCollection<Listing<DataCollection<Link>>>>(jsonArray[0], typeA)
        val second =
            gson.fromJson<DataCollection<Listing<DataCollection<Comment>>>>(jsonArray[1], typeB)

        return Post(data=first.data.children.first().data, comments = second.data.children.map { it.data })
    }

}

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

        val gson = GsonBuilder().registerTypeAdapter(
            Post::class.javaObjectType,
            CustomDeserializer(Gson())
        ).create()

        return Retrofit.Builder()
            .baseUrl("https://api.reddit.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(APIService::class.java)
    }

}