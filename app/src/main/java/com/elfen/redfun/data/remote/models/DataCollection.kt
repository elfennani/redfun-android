package com.elfen.redfun.data.remote.models

data class DataCollection<T>(
    val kind: String,
    val data: T,
)
