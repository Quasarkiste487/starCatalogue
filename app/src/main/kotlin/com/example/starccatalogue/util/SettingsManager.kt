package com.example.starccatalogue.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class AppLanguage(val code: String) {
    SYSTEM(""),
    GERMAN("de"),
    ENGLISH("en"),
    FRENCH("fr"),
    SPANISH("es"),
    ITALIAN("it")
}

@JsonClass(generateAdapter = true)
data class SettingsData(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM
)

interface Settings {
    val settingsFlow: StateFlow<SettingsData>
    suspend fun initialize()
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setLanguage(language: AppLanguage)
    fun applyLanguage()
}

class SettingsManager(
    context: Context,
    moshi: Moshi,
    private val logger: Logger
) : Settings {

    private val file: File = File(context.filesDir, "settings.json")

    @OptIn(ExperimentalStdlibApi::class)
    private val adapter = moshi.adapter<SettingsData>()

    private val lock = Any()
    private var current = SettingsData() // Default until loaded

    private val _settingsFlow = MutableStateFlow(current)
    override val settingsFlow: StateFlow<SettingsData> = _settingsFlow.asStateFlow()

    override suspend fun initialize() {
        current = loadSettings()
        _settingsFlow.value = current
        applyLanguage()
    }

    private suspend fun loadSettings(): SettingsData = withContext(Dispatchers.IO) {
        synchronized(lock) {
            if (file.exists()) {
                try {
                    adapter.fromJson(file.readText()) ?: SettingsData()
                } catch (e: Exception) {
                    logger.e("SettingsManager", "Failed to load settings: ${e.message}", e)
                    SettingsData()
                }
            } else {
                SettingsData()
            }
        }
    }

    private suspend fun persist(data: SettingsData) = withContext(Dispatchers.IO) {
        synchronized(lock) {
            val json = adapter.toJson(data)
            file.writeText(json)
            current = data
        }
        _settingsFlow.value = data
        logger.i("SettingsManager", "Saved: ${adapter.toJson(data)}")
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        persist(current.copy(themeMode = mode))
    }

    override suspend fun setLanguage(language: AppLanguage) {
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
