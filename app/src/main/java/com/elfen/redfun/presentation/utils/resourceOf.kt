package com.elfen.redfun.presentation.utils

import android.database.sqlite.SQLiteException
import com.google.gson.JsonParseException
import okio.IOException

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

suspend fun Throwable.toResource(): Resource.Error<Any> = when (this) {
    is IOException -> Resource.Error("Not connected to the internet")
    is retrofit2.HttpException -> Resource.Error("Something went wrong")
    is JsonParseException -> Resource.Error("Failed to parse json")
    is SQLiteException -> Resource.Error("Failed to save to database")
    else -> Resource.Error("Something went wrong")
}

suspend fun <T> resourceOf(call: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(call())
    } catch (e: IOException) {
        e.printStackTrace()
        Resource.Error("Not connected to the internet")
    } catch (e: retrofit2.HttpException) {
        e.printStackTrace()
        Resource.Error(if (e.code() == 404) "Result not found" else "something went wrong")
    } catch (e: JsonParseException) {
        e.printStackTrace()
        Resource.Error("Failed to parse json")
    } catch (e: SQLiteException) {
        e.printStackTrace()
        Resource.Error("Failed to save to database")
    } catch (e: Exception) {
        e.printStackTrace()
        Resource.Error("Something went wrong")
    }
}