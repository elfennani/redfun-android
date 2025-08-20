package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.repository.FeedRepository
import javax.inject.Inject

class GetSortingUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke() = feedRepository.sortingFlow
}