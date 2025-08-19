package com.elfen.redfun.ui.screens.settings

data class ResolutionOption(
    val label: String,
    val value: Int
)

val resolutionOptions = listOf(
    ResolutionOption("Auto", 0),
    ResolutionOption("360p", 360),
    ResolutionOption("720p", 720),
    ResolutionOption("1080p", 1080),
    ResolutionOption("1440p", 1440),
    ResolutionOption("2160p", 2160)
)