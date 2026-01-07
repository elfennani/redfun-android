package com.elfen.redfun.presentation.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun AppActionButton(
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
