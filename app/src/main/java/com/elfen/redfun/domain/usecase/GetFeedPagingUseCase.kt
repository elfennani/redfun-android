package com.elfen.redfun.domain.usecase

import androidx.paging.map
import com.elfen.redfun.data.mappers.asDomainModel
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.repository.FeedRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFeedPagingUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(feed: Feed) = feedRepository.getFeedPaging(feed).map {
        it.map { post ->
            post.asDomainModel()
        }
    }
}