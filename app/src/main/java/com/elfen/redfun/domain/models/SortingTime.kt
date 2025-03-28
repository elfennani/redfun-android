package com.elfen.redfun.domain.models

enum class SortingTime{
    HOUR, DAY, WEEK, MONTH, YEAR, ALL_TIME;
}

fun SortingTime.toParameter(): String = when(this){
    SortingTime.HOUR -> "hour"
    SortingTime.DAY -> "day"
    SortingTime.WEEK -> "week"
    SortingTime.MONTH -> "month"
    SortingTime.YEAR -> "year"
    SortingTime.ALL_TIME -> "all"
}

fun SortingTime.toLabel(): String = when(this){
    SortingTime.HOUR -> "hour"
    SortingTime.DAY -> "day"
    SortingTime.WEEK -> "week"
    SortingTime.MONTH -> "month"
    SortingTime.YEAR -> "year"
    SortingTime.ALL_TIME -> "all time"

}