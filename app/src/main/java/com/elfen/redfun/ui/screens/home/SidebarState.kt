package com.elfen.redfun.ui.screens.home

import com.elfen.redfun.domain.models.Profile

data class SidebarState(
    val isLoading: Boolean = true,
    val user: Profile? = null
)
