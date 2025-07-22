package com.elfen.redfun.data.remote.models

data class PostDetails (
    val post: Link,
    val comments: List<RemoteComment>
)