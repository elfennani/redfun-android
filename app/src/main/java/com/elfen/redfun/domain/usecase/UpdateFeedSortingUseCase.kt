package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.repository.FeedRepository
import javax.inject.Inject

class UpdateFeedSortingUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(feed: Feed, sorting: Sorting) {
        feedRepository.setSortingForFeed(feed, sorting)
    }
}