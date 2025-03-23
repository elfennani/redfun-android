package com.elfen.redfun.data.remote

import com.elfen.redfun.data.remote.models.RemoteComment
import com.elfen.redfun.data.remote.models.DataCollection
import com.elfen.redfun.data.remote.models.Link
import com.elfen.redfun.data.remote.models.Listing
import com.elfen.redfun.data.remote.models.PostDetails
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PostDetailsDeserializer(
    private val gson: Gson
) : JsonDeserializer<PostDetails> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PostDetails {
        val jsonArray = json.asJsonArray
        if (jsonArray.size() != 2) throw JsonParseException("Expected an array of size 2")

        val typeA = object : TypeToken<DataCollection<Listing<DataCollection<Link>>>>() {}.type
        val typeB = object : TypeToken<DataCollection<Listing<DataCollection<RemoteComment>>>>() {}.type

        val first =
            gson.fromJson<DataCollection<Listing<DataCollection<Link>>>>(jsonArray[0], typeA)
        val second =
            gson.fromJson<DataCollection<Listing<DataCollection<RemoteComment>>>>(jsonArray[1], typeB)

        return PostDetails(post=first.data.children.first().data, comments = second.data.children.map { it.data })
    }

}