package com.elfen.redfun.domain.models

sealed class Comment{
    data class Body(val id: String,val body: String): Comment()
    data class More(val id: String): Comment()
}