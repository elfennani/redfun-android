package com.elfen.redfun.data.remote

import com.elfen.redfun.data.remote.models.RemoteComment
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class CommentDeserializer : JsonDeserializer<RemoteComment> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): RemoteComment {
        val jsonObject = json.asJsonObject

        return when {
            jsonObject.has("body") -> context.deserialize<RemoteComment.Body>(jsonObject, RemoteComment.Body::class.java)
            jsonObject.has("count") -> context.deserialize<RemoteComment.More>(jsonObject, RemoteComment.More::class.java)
            else -> throw JsonParseException("Unknown comment type")
        }
    }
}