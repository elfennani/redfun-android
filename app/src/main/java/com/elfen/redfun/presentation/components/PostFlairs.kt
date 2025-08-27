package com.elfen.redfun.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PostFlairs(
    modifier: Modifier = Modifier,
    isNSFW: Boolean = false,
    flair: String? = null,
    navigateFlair: (String) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        if (isNSFW) {
            Box(
                modifier = Modifier
                    .background(Color.Red.copy(alpha = 0.2f), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "NSFW",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
        if (flair != null) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { navigateFlair(flair) }
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    flair,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }

}