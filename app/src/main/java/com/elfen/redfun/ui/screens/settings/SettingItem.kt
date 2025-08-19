package com.elfen.redfun.ui.screens.settings

sealed class SettingItem {
    data class Switch(
        val title: String,
        val description: String? = null,
        val isChecked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : SettingItem()

    data class Options(
        val title: String,
        val description: String? = null,
        val options: List<String>,
        val selectedOptionIndex: Int,
        val onOptionSelected: (Int) -> Unit
    ) : SettingItem()

    data class Button(
        val title: String,
        val description: String? = null,
        val onClick: () -> Unit
    ) : SettingItem()

    data class Text(
        val title: String,
        val description: String? = null
    ) : SettingItem()
}
