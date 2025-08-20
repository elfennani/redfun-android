package com.elfen.redfun.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.elfen.redfun.data.local.models.PostEntity
import com.elfen.redfun.data.local.models.PostMediaEntity
import com.elfen.redfun.domain.model.MediaImage
import com.elfen.redfun.domain.model.MediaVideo
import com.elfen.redfun.domain.model.Post
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class PostWithMedia(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val media: List<PostMediaEntity>
)