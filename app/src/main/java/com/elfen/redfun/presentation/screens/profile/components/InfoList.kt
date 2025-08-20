package com.elfen.redfun.presentation.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@DslMarker
annotation class InfoListDsl

@InfoListDsl
class InfoListScope {
    val items = mutableListOf<Pair<String, String>>()

    fun item(title: String, value: String) {
        items += (title to value)
    }
}

@Composable
fun InfoList(modifier: Modifier = Modifier, content: InfoListScope.() -> Unit) {
    val scope = InfoListScope().apply(content)
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) {
        scope.items.forEach { (title, value) ->
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    value,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Text(title, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}