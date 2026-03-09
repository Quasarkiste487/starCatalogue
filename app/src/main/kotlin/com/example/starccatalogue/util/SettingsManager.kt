package com.example.starccatalogue.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class AppLanguage(val displayName: String, val code: String) {
    SYSTEM("System", ""),
    GERMAN("Deutsch", "de"),
    ENGLISH("English", "en"),
    FRENCH("Français", "fr"),
    SPANISH("Español", "es"),
    ITALIAN("Italiano", "it")
}

@JsonClass(generateAdapter = true)
data class SettingsData(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM
)

interface Settings {
    val settingsFlow: StateFlow<SettingsData>
    fun setThemeMode(mode: ThemeMode)
    fun setLanguage(language: AppLanguage)
    fun applyLanguage()
}

class SettingsManager(
    private val context: Context,
    private val moshi: Moshi,
    private val logger: Logger
) : Settings {

    private val file: File = File(context.filesDir, "settings.json")

    @OptIn(ExperimentalStdlibApi::class)
    private val adapter = moshi.adapter<SettingsData>()

    private val lock = Any()
    private var current = loadSettings()

    private val _settingsFlow = MutableStateFlow(current)
    override val settingsFlow: StateFlow<SettingsData> = _settingsFlow.asStateFlow()

    private fun loadSettings(): SettingsData {
        return synchronized(lock) {
            if (file.exists()) {
                try {
                    adapter.fromJson(file.readText()) ?: SettingsData()
                } catch (e: Exception) {
                    logger.e("SettingsManager", "Failed to load settings: ${e.message}")
                    SettingsData()
                }
            } else {
                SettingsData()
            }
        }
    }

    private fun persist(data: SettingsData) {
        synchronized(lock) {
            val json = adapter.toJson(data)
            file.writeText(json)
            current = data
            _settingsFlow.value = data
            logger.i("SettingsManager", "Saved: $json")
        }
    }

    override fun setThemeMode(mode: ThemeMode) {
        persist(current.copy(themeMode = mode))
    }

    override fun setLanguage(language: AppLanguage) {
        persist(current.copy(language = language))
        applyLanguage()
    }

    override fun applyLanguage() {
        val localeList = if (current.language == AppLanguage.SYSTEM || current.language.code.isEmpty()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(current.language.code)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
        logger.i("SettingsManager", "Applied language: ${current.language.code.ifEmpty { "system" }}")
    }
}
