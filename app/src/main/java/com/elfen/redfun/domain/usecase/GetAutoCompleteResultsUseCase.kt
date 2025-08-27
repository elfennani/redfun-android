package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.repository.SearchRepository
import javax.inject.Inject

class GetAutoCompleteResultsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String) =
        searchRepository.getAutoCompleteResults(query)
}