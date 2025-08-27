package com.elfen.redfun.presentation.components

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun PostListWrapper(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onScrollToTop: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val minOffset = 0.dp
    val maxOffset = 128.dp
    val minOpacity = 0f
    val maxOpacity = 1f
    var offsetX by remember { mutableStateOf(maxOffset) }
    var opacity by remember { mutableFloatStateOf(1f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = consumed.y
                val newOffset = offsetX - delta.dp / 4
                val newOpacity = opacity + (delta / 800f)
                opacity = newOpacity.coerceIn(minOpacity, maxOpacity)

                offsetX = newOffset.coerceIn(minOffset, maxOffset)

                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LaunchedEffect(enabled) {
        if (!enabled) {
            scope.launch {
                animate(initialValue = offsetX.value, targetValue = maxOffset.value) { value, _ ->
                    offsetX = value.dp
                }
            }
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
    ) {
        content()
        FloatingActionButton(
            modifier = Modifier
                .alpha(opacity)
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .offset(x = if(enabled) offsetX else maxOffset),
            onClick = { onScrollToTop() }
        ) {
            Icon(Icons.Default.ArrowUpward, null)
        }
    }
}