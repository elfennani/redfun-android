package com.elfen.redfun.data

import com.elfen.redfun.data.mappers.asDomainModel
import com.elfen.redfun.data.remote.AuthAPIService
import com.elfen.redfun.domain.model.AutoCompleteResult
import com.elfen.redfun.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: AuthAPIService
) : SearchRepository {
    override suspend fun getAutoCompleteResults(query: String): AutoCompleteResult {
        val subreddits = apiService.getSubredditSuggestions(query)
        val users = apiService.getUserSuggestions(query)

        return AutoCompleteResult(
            subreddits = subreddits.data.children.map { it.data.asDomainModel() },
            users = users.data.children.filter { !it.data.isSuspended && !it.data.isBlocked }.map { it.data.asDomainModel() }
        )
    }
}