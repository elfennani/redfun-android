package com.elfen.redfun.domain.models

sealed class Sorting(val feed:String,val shouldSaveCursor: Boolean) {
    data object Best: Sorting("best", true)
    data object Hot: Sorting("hot", true)
    data object New: Sorting("new", false)
    data object Rising: Sorting("rising", false)
    data class Top(val time: SortingTime): Sorting("top", false)
    data class Controversial(val time: SortingTime): Sorting("controversial", false)
}

fun Sorting.getTimeParameter(): String? = when(this){
    is Sorting.Top -> time.toParameter()
    is Sorting.Controversial -> time.toParameter()
    else -> null
}