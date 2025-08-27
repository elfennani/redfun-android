package com.elfen.redfun.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.icon
import com.elfen.redfun.domain.model.toLabel
import com.elfen.redfun.presentation.screens.feed.FeedEvent
import com.elfen.redfun.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListPrefs(
    modifier: Modifier = Modifier,
    displayMode: DisplayMode,
    sorting: Sorting? = null,
    navBarShown: Boolean? = null,
    onSelectSorting: (Sorting) -> Unit = {},
    onSelectDisplayMode: (DisplayMode) -> Unit = {},
    onToggleNavBar: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var sortingSheet by remember { mutableStateOf(false) }
    var viewModeSheet by remember { mutableStateOf(false) }

    if (viewModeSheet) {
        DisplayModeBottomSheet(
            onDismissRequest = { viewModeSheet = false },
            current = displayMode,
            onSelectDisplayMode = { mode -> onSelectDisplayMode(mode); }
        )
    }
    if (sortingSheet) {
        SortingBottomSheet(
            onDismissRequest = { sortingSheet = false },
            onSelectSorting = { sort -> onSelectSorting(sort); },
            sorting = sorting,
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (sorting != null) {
            FilledTonalButton(
                onClick = { sortingSheet = true },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Sort, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(sorting.toLabel(), style = MaterialTheme.typography.titleSmall)
            }
        }
        Spacer(Modifier.weight(1f))
        if (navBarShown != null) {
            FilledTonalIconButton(onClick = { onToggleNavBar() }) {
                Icon(
                    if (navBarShown) Icons.Default.ArrowDropDown
                    else Icons.Default.ArrowDropUp,
                    contentDescription = null,
                )
            }
        }
        FilledTonalButton(onClick = { viewModeSheet = true }) {
            Icon(painterResource(displayMode.icon()), null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(displayMode.toLabel(), style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Preview
@Composable
private fun PostListPrefsPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            PostListPrefs(
                modifier = Modifier.fillMaxWidth(),
                displayMode = DisplayMode.LIST,
                sorting = Sorting.Hot
            )
        }
    }
}