package com.elfen.redfun.domain.model

sealed class Sorting(val feed:String,val shouldSaveCursor: Boolean) {
    data object Best: Sorting("best", true)
    data object Hot: Sorting("hot", true)
    data object New: Sorting("new", false)
    data object Rising: Sorting("rising", false)
    data class Top(val time: SortingTime): Sorting("top", false)
    data class Controversial(val time: SortingTime): Sorting("controversial", false);

    companion object{
        fun fromName(name: String): Sorting = when(name){
            "best" -> Best
            "hot" -> Hot
            "new" -> New
            "rising" -> Rising
            else -> {
                val parts = name.split("-")
                when(parts[0]){
                    "top" -> Top(SortingTime.valueOf(parts[1]))
                    "controversial" -> Controversial(SortingTime.valueOf(parts[1]))
                    else -> throw IllegalArgumentException("Unknown sorting name: $name")
                }
            }
        }
    }
}

fun Sorting.getTimeParameter(): String? = when(this){
    is Sorting.Top -> time.toParameter()
    is Sorting.Controversial -> time.toParameter()
    else -> null
}

fun Sorting.name(): String = when(this){
    is Sorting.Best -> "best"
    is Sorting.Hot -> "hot"
    is Sorting.New -> "new"
    is Sorting.Rising -> "rising"
    is Sorting.Top -> "top-${time.name}"
    is Sorting.Controversial -> "controversial-${time.name}"
}

fun Sorting.toLabel(): String = when(this){
    is Sorting.Best -> "Best"
    is Sorting.Hot -> "Hot"
    is Sorting.New -> "New"
    is Sorting.Rising -> "Rising"
    is Sorting.Top -> "Top (${time.toLabel()})"
    is Sorting.Controversial -> "Controversial (${time.toLabel()})"
}