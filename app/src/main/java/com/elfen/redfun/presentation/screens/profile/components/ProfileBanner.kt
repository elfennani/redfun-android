package com.elfen.redfun.presentation.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProfileBanner(modifier: Modifier = Modifier, banner: String) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = banner,
            contentDescription = "Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .background(if (LocalInspectionMode.current) Color.LightGray else Color.Transparent)
                .aspectRatio(21f / 9f),
        )
        val colorStops = arrayOf(
            0.5f to Color.Black.copy(alpha = 0f),
            1f to Color.Black.copy(alpha = 0.5f),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(colorStops = colorStops))
        )
    }
}