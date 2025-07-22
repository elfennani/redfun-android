package com.elfen.redfun.domain.models

sealed class Feed {
  data class Home(val sorting: Sorting): Feed()
  data class Subreddit(val subreddit: String, val sorting: Sorting): Feed()

  data object SavedPosts: Feed()
}

fun Feed.name(): String {
  return when (this) {
    is Feed.Home -> "home-${sorting.feed}"
    is Feed.SavedPosts -> "saved-posts"
    is Feed.Subreddit -> "subreddit-${subreddit}-${sorting.feed}"
  }
}