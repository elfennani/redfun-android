package com.elfen.redfun.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.icon
import com.elfen.redfun.presentation.components.ui.AppBottomSheet
import com.elfen.redfun.presentation.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayModeBottomSheet(
    onDismissRequest: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(true),
    current: DisplayMode = DisplayMode.MASONRY,
    onSelectDisplayMode: (DisplayMode) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    AppBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        title = {
            Icon(Icons.Rounded.ViewDay, null)
            Text("Display Mode")
        }
    ) {
        DisplayModeView(
            current = current,
            onSelectDisplayMode = { mode ->
                onSelectDisplayMode(mode)
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onDismissRequest()
                    }
                }
            }
        )
    }
}

@Composable
private fun DisplayModeView(
    current: DisplayMode = DisplayMode.MASONRY,
    onSelectDisplayMode: (DisplayMode) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DisplayMode.entries.forEach { mode ->
            DisplayModeItem(
                mode = mode,
                isSelected = current == mode,
                onClick = {
                    onSelectDisplayMode(it)
                }
            )
        }
    }
}

@Composable
private fun RowScope.DisplayModeItem(
    mode: DisplayMode,
    isSelected: Boolean = false,
    onClick: (DisplayMode) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(MaterialTheme.shapes.large)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick(mode) }
            .padding(horizontal = 8.dp, vertical = 24.dp)
            .fillMaxWidth()
            .align(Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Icon(
            painterResource(mode.icon()),
            null,
            modifier = Modifier.size(40.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DisplayModeViewPreview() {
    AppTheme {
        DisplayModeView(
            current = DisplayMode.SCROLLER,
            onSelectDisplayMode = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DisplayModeSheetPreview(){
    AppTheme {
        DisplayModeBottomSheet(
            onDismissRequest = {},
            sheetState = rememberModalBottomSheetState(true),
            current = DisplayMode.SCROLLER,
            onSelectDisplayMode = {}
        )
    }
}