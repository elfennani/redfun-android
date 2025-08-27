package com.elfen.redfun.domain.repository

import com.elfen.redfun.domain.model.AutoCompleteResult

interface SearchRepository {
    suspend fun getAutoCompleteResults(query: String): AutoCompleteResult
}