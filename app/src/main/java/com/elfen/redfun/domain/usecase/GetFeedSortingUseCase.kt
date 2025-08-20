package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.repository.FeedRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetFeedSortingUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(feed: Feed) = feedRepository
        .getSortingForFeed(feed)
        .distinctUntilChanged()
}