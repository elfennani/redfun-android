package com.elfen.redfun.domain.models

import com.elfen.redfun.R

enum class DisplayMode {
    MASONRY, SCROLLER
}

fun DisplayMode.icon() = when (this) {
    DisplayMode.MASONRY -> R.drawable.circum__grid_4_2
    DisplayMode.SCROLLER -> R.drawable.solar__posts_carousel_vertical_bold_duotone
}