package com.elfen.redfun.presentation.screens.search.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elfen.redfun.domain.model.Profile

@Composable
fun ProfileSearchItem(modifier: Modifier = Modifier, profile: Profile) {
    SearchItem(
        modifier = modifier,
        icon = profile.icon,
        title = "u/${profile.username}",
        subtitle = "${profile.totalKarma.shorten()} Karma",
        isNSFW = false
    )
}