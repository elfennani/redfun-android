package com.elfen.redfun.presentation.screens.search

import com.elfen.redfun.domain.model.AutoCompleteResult

data class SearchUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val autoCompleteResult: AutoCompleteResult? = null,
)
