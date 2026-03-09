package com.example.starccatalogue.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starccatalogue.util.AppLanguage
import com.example.starccatalogue.util.Bookmarks
import com.example.starccatalogue.util.Settings
import com.example.starccatalogue.util.SettingsData
import com.example.starccatalogue.util.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val settings: Settings,
    private val bookmarks: Bookmarks
) : ViewModel() {

    val settingsState: StateFlow<SettingsData> = settings.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, settings.settingsFlow.value)

    fun setThemeMode(mode: ThemeMode) {
        settings.setThemeMode(mode)
    }

    fun setLanguage(language: AppLanguage) {
        settings.setLanguage(language)
    }

    fun clearBookmarks() {
        bookmarks.clearBookmarks()
    }
}

