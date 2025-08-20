package com.elfen.redfun.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.SortingTime

@Entity(tableName = "sorting")
data class SortingEntity(
    @PrimaryKey val userId: String,
    val sorting: String,
    val time: String?,
)

fun Sorting.toEntity(userId: String) = SortingEntity(
    userId = userId,
    sorting = feed,
    time = when(this){
        is Sorting.Top -> this.time.name
        is Sorting.Controversial -> this.time.name
        else -> null
    }
)

fun SortingEntity.toDomain() = when(this.sorting){
    "top" -> Sorting.Top(SortingTime.valueOf(time!!))
    "controversial" -> Sorting.Controversial(SortingTime.valueOf(time!!))
    "new" -> Sorting.New
    "rising" -> Sorting.Rising
    "hot" -> Sorting.Hot
    "best" -> Sorting.Best
    else -> throw IllegalArgumentException("Unknown sorting")
}
