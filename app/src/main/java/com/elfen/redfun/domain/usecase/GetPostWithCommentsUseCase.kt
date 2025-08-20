package com.elfen.redfun.domain.usecase

import com.elfen.redfun.domain.repository.FeedRepository
import javax.inject.Inject

class GetPostWithCommentsUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(postId: String) = feedRepository.getPostWithComments(postId)
}