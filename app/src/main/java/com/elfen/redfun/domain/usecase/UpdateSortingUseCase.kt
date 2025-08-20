package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.repository.FeedRepository
import javax.inject.Inject

class UpdateSortingUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(sorting: Sorting) {
        feedRepository.setSorting(sorting)
    }
}