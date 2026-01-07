package com.elfen.redfun.presentation.components.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elfen.redfun.presentation.theme.AppTheme

@Composable
fun AppNavigationBar(
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    val gradientColors = listOf(
        Color.Black.copy(alpha = 0f),
        Color.Black.copy(alpha = 0.33f)
    )
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val brush = Brush.verticalGradient(gradientColors)
                drawRect(brush)
            }
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .then(
                    if(!isDarkTheme)
                        Modifier.shadow(4.dp, CircleShape, spotColor = MaterialTheme.colorScheme.onSurface)
                    else
                        Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                )
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(4.dp),
            content = content
        )
        action()
    }
}

@Composable
fun AppNavigationBarTab(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit = {},
) {
    IconButton(
        modifier = modifier.size(48.dp),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        icon()
    }
}

/**
 * An icon button with an aspect ratio of 1:1.
 */
@Composable
fun AppNavigationBarIconAction(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit = {},
) {
    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .then(
                if(!isDarkTheme)
                    Modifier.shadow(4.dp, CircleShape, spotColor = MaterialTheme.colorScheme.onSurface)
                else
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .padding(4.dp)
    ) {
        IconButton(
            modifier = modifier.size(48.dp),
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            content()
        }
    }
}

/**
 * A text and icon bottom action button
 */
@Composable
fun AppBottomActionButton(modifier: Modifier = Modifier) {

}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
private fun AppNavigationBarPreview() {
    AppTheme() {
        Scaffold(
            bottomBar = {
                AppNavigationBar(
                    action = {
                        AppNavigationBarIconAction(
                            content = {
                                Icon(Icons.Default.Search, "Search")
                            },
                            onClick = {
                                // TODO: Search
                            }
                        )
                    }
                ) {
                    AppNavigationBarTab(
                        selected = true,
                        icon = {
                            Icon(Icons.Default.Home, "Home")
                        }
                    )
                    AppNavigationBarTab(
                        selected = false,
                        icon = {
                            Icon(Icons.Default.Map, "Map")
                        }
                    )
                    AppNavigationBarTab(
                        selected = false,
                        icon = {
                            Icon(Icons.Default.Tab, "Tab")
                        }
                    )
                }
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