package com.elfen.redfun.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.SortingTime
import com.elfen.redfun.domain.model.getTimeParameter
import com.elfen.redfun.domain.model.toLabel
import com.elfen.redfun.domain.model.toParameter
import com.elfen.redfun.presentation.components.ui.AppBottomSheet
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingBottomSheet(
    onDismissRequest: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(true),
    onSelectSorting: (Sorting) -> Unit = {},
    sorting: Sorting? = null,
) {
    val context = LocalContext.current
    val dataStore = context.dataStore
    val coroutineScope = rememberCoroutineScope()
    val isSheetVisible by remember {
        derivedStateOf {
            sheetState.isVisible
        }
    }
    var tempSorting by remember {
        mutableStateOf<Sorting?>(
            when (sorting) {
                is Sorting.Top -> Sorting.Top(sorting.time)
                is Sorting.Controversial -> Sorting.Controversial(sorting.time)
                else -> null
            }
        )
    }

    val updateSorting = { sorting: Sorting ->
        onSelectSorting(sorting)
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismissRequest()
            }
        }
    }
    val baseSortingList = listOf(Sorting.Best, Sorting.Hot, Sorting.New, Sorting.Rising)

    AppBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        title = {
            Icon(Icons.Rounded.Sort, null)
            Text("Sorting")
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val isActive: (Sorting) -> Boolean = { currentSorting ->
                    val activeSorting = tempSorting ?: sorting
                    when (activeSorting) {
                        is Sorting.Top, is Sorting.Controversial -> currentSorting::class == activeSorting::class
                        else -> currentSorting == activeSorting
                    }
                }

                baseSortingList.forEach { item ->
                    Button(
                        onClick = { updateSorting(item) },
                        colors = if (isActive(item)) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text(
                            text = item.feed.replaceFirstChar { char ->
                                if (char.isLowerCase()) char.titlecase(
                                    Locale.US
                                ) else char.toString()
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Button(
                    onClick = { tempSorting = Sorting.Top(SortingTime.ALL_TIME); },
                    colors = if (isActive(Sorting.Top(SortingTime.ALL_TIME)) || tempSorting is Sorting.Top) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text(
                        "Top",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Button(
                    onClick = {
                        tempSorting = Sorting.Controversial(SortingTime.ALL_TIME);
                    },
                    colors = if (isActive(Sorting.Controversial(SortingTime.ALL_TIME)) || tempSorting is Sorting.Controversial) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text(
                        "Controversial",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            AnimatedVisibility(visible = tempSorting != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "TIME PERIOD",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        SortingTime.entries.forEach { time ->
                            Button(
                                onClick = {
                                    when (tempSorting) {
                                        is Sorting.Top -> updateSorting(Sorting.Top(time))
                                        is Sorting.Controversial -> updateSorting(
                                            Sorting.Controversial(
                                                time
                                            )
                                        )

                                        else -> {}
                                    }
                                },
                                colors = if (tempSorting?.getTimeParameter() == time.toParameter()) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Text(
                                    time.toLabel(),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}