package com.elfen.redfun.ui.screens.profile.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.elfen.redfun.ui.theme.AppTheme

@Composable
fun ProfileIcon(modifier: Modifier = Modifier, icon: String?) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(2.dp, MaterialTheme.colorScheme.inversePrimary, CircleShape)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            AsyncImage(
                model = icon,
                contentDescription = "Banner",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun ProfileIconPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp)
        ) {
            ProfileIcon(icon = "https://example.com/icon.png")
        }
    }
}