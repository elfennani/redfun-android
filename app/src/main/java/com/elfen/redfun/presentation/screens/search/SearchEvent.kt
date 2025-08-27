package com.elfen.redfun.presentation.screens.search

sealed class SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent()
    object ClearQuery : SearchEvent()
}