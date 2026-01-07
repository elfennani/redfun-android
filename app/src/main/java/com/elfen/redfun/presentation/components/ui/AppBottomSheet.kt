package com.elfen.redfun.presentation.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.elfen.redfun.presentation.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(true),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    containerPadding: PaddingValues = PaddingValues(16.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    shape: Shape = MaterialTheme.shapes.extraLarge,
    title: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RectangleShape,
        scrimColor = Color.Black.copy(0.5f),
        dragHandle = {
        },
        containerColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier.padding(containerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .then(
                            if (!isDarkTheme)
                                Modifier.shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape,
                                    spotColor = contentColor.copy(
                                        alpha = 0.5f
                                    )
                                )
                            else
                                Modifier.border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    CircleShape
                                )
                        )
                        .background(containerColor, CircleShape)
                        .padding(
                            vertical = 10.dp,
                            horizontal = contentPadding.calculateLeftPadding(
                                LayoutDirection.Ltr
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.titleSmall,
                        LocalContentColor provides contentColor
                    ) {
                        title()
                    }
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismissRequest()
                            }
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                    ),
                ) {
                    Icon(Icons.Default.Clear, null)
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .then(
                        if (!isDarkTheme)
                            Modifier.shadow(
                                8.dp,
                                shape,
                                spotColor = contentColor.copy(alpha = 0.5f)
                            )
                        else
                            Modifier.border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                shape
                            )
                    )
                    .background(containerColor, shape)
                    .clip(shape)
                    .padding(contentPadding),
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AppBottomSheetPreview() {
    val state = rememberModalBottomSheetState(true)
    AppTheme {
        AppBottomSheet(
            modifier = Modifier,
            sheetState = state,
            onDismissRequest = {},
            title = {
                Text("Title")
            }
        ) {
            Text("Hello World")
            Text("Hello World")
            Text("Hello World")
        }
    }
}