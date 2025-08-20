package com.elfen.redfun.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.elfen.redfun.data.local.models.FeedPostEntity
import com.elfen.redfun.data.local.models.PostEntity
import com.elfen.redfun.data.local.models.PostMediaEntity

data class FeedWithPost(
    @Embedded val feed: FeedPostEntity,
    @Relation(
        parentColumn = "postId",
        entityColumn = "id"
    )
    val post: PostEntity,

    @Relation(
        parentColumn = "postId",
        entityColumn = "postId",
    )
    val media: List<PostMediaEntity>
)
