package com.elfen.redfun.presentation.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elfen.redfun.presentation.theme.AppTheme

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground
) {
    val gradientColors = listOf(
        containerColor.copy(0.75f),
        containerColor.copy(0.5f),
        containerColor.copy(alpha = 0f),
    )
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val brush =
                    Brush.verticalGradient(gradientColors, startY = statusBarHeight.toFloat())
                drawRect(brush)
            }
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(16.dp)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleLarge,
            LocalContentColor provides contentColor
        ) {
            title()
        }
    }
}

@Preview
@Composable
private fun AppTopBarPreview() {
    AppTheme() {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = {
                        Text("Feed")
                    }
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 1..10) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .background(
                                Color.LightGray
                            )
                    )
                }
            }

        }
    }
}