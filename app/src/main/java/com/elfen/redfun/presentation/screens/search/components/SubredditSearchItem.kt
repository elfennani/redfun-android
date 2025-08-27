package com.elfen.redfun.presentation.screens.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.elfen.redfun.domain.model.Subreddit

fun Int.shorten(): String {
    return when {
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
        else -> this.toString()
    }
}

@Composable
fun SubredditSearchItem(modifier: Modifier = Modifier, subreddit: Subreddit) {
    SearchItem(
        modifier = modifier,
        icon = subreddit.iconUrl,
        title = "r/${subreddit.name}",
        subtitle = "${subreddit.subscribers.shorten()} members",
        isNSFW = subreddit.isNSFW
    )
}