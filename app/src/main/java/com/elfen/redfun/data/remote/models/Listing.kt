package com.elfen.redfun.data.remote.models

data class Listing<T>(
    val after: String?,
    val before: String?,
    val children: List<T>,
    val dist: Int,
)
