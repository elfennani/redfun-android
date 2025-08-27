package com.elfen.redfun.presentation.screens.search.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elfen.redfun.domain.model.Subreddit
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
fun Int.shorten(): String {
    return when {
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
        else -> this.toString()
    }
}

private const val TAG = "SubredditSearchItem"

@Composable
fun SubredditSearchItem(
    modifier: Modifier = Modifier,
    subreddit: Subreddit,
    onSelectSubreddit: (String) -> Unit = {}
) {
    val maxDragAmount = 256f
    val scope = rememberCoroutineScope()
    var draggedAmount by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        if(draggedAmount > maxDragAmount * 0.75f) {
                            onSelectSubreddit(subreddit.name)
                        }

                        scope.launch {
                            animate(
                                initialValue = draggedAmount,
                                targetValue = 0f
                            ) { value, _ ->
                                draggedAmount = value
                            }
                        }.invokeOnCompletion {
                            isDragging = false
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            animate(
                                initialValue = draggedAmount,
                                targetValue = 0f
                            ) { value, _ ->
                                draggedAmount = value
                            }
                        }.invokeOnCompletion {
                            isDragging = false
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        draggedAmount += dragAmount
                        draggedAmount = draggedAmount.coerceIn(0f, maxDragAmount)
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp))
            Text(
                "Search",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
        SearchItem(
            modifier = Modifier
                .clip(CircleShape)
                .offset(
                    x = (if (isDragging) with(density) { draggedAmount.toDp() } else 0.dp)
                )
                .background(MaterialTheme.colorScheme.surface, CircleShape),
            icon = subreddit.iconUrl,
            title = "r/${subreddit.name}",
            subtitle = "${subreddit.subscribers.shorten()} members",
            isNSFW = subreddit.isNSFW
        )
    }
}