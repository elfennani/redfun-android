package com.elfen.redfun.domain.model

import com.elfen.redfun.R

enum class DisplayMode {
    MASONRY, SCROLLER, LIST
}

fun DisplayMode.icon() = when (this) {
    DisplayMode.MASONRY -> R.drawable.circum__grid_4_2
    DisplayMode.SCROLLER -> R.drawable.solar__posts_carousel_vertical_bold_duotone
    DisplayMode.LIST -> R.drawable.outline_lists_24
}