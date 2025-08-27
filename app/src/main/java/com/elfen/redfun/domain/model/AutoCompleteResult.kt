package com.elfen.redfun.domain.model

data class AutoCompleteResult(
    val subreddits: List<Subreddit> = emptyList(),
    val users: List<Profile> = emptyList()
)
