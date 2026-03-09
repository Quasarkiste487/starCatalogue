package com.example.starccatalogue.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.starccatalogue.R
import com.example.starccatalogue.util.AppLanguage
import com.example.starccatalogue.util.ThemeMode
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (_: Exception) {
            "1.0"
        }
    }

    val (showClearDialog, setShowClearDialog) = remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { setShowClearDialog(false) },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null) },
            title = { Text(stringResource(R.string.reset_bookmarks_dialog_title)) },
            text = { Text(stringResource(R.string.reset_bookmarks_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearBookmarks()
                    setShowClearDialog(false)
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { setShowClearDialog(false) }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings_title)) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Erscheinungsbild ─────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.appearance), icon = Icons.Default.Palette) {
                Text(
                    text = stringResource(R.string.color_scheme),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val modes = listOf(
                        Triple(ThemeMode.LIGHT, stringResource(R.string.theme_light), Icons.Default.LightMode),
                        Triple(ThemeMode.SYSTEM, stringResource(R.string.theme_system), Icons.Default.BrightnessAuto),
                        Triple(ThemeMode.DARK, stringResource(R.string.theme_dark), Icons.Default.DarkMode)
                    )
                    modes.forEachIndexed { index, (mode, label, icon) ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                            onClick = { viewModel.setThemeMode(mode) },
                            selected = state.themeMode == mode,
                            label = { Text(label) },
                            icon = {
                                SegmentedButtonDefaults.Icon(active = state.themeMode == mode) {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(SegmentedButtonDefaults.IconSize))
                                }
                            }
                        )
                    }
                }
            }

            // ── Sprache ──────────────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.language), icon = Icons.Default.Language) {
                ExposedDropdownMenuBox(
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = it }
                ) {
                    OutlinedTextField(
                        value = getLanguageDisplayName(state.language),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_language)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false }
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(getLanguageDisplayName(lang)) },
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    languageExpanded = false
                                },
                                trailingIcon = {
                                    if (state.language == lang) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // ── Lesezeichen ──────────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.bookmarks_section), icon = Icons.Default.Bookmark) {
                OutlinedButton(
                    onClick = { setShowClearDialog(true) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.reset_bookmarks))
                }
            }

            // ── Über die App ─────────────────────────────────────────────────
            SettingsSection(
                title = stringResource(R.string.about_app),
                icon = Icons.Default.Info,
                trailingAction = {
                    IconButton(onClick = { showAbout = !showAbout }) {
                        Icon(
                            if (showAbout) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                }
            ) {
                if (showAbout) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        AboutRow(label = stringResource(R.string.about_app_name), value = stringResource(R.string.about_app_name_value))
                        AboutRow(label = stringResource(R.string.about_version), value = versionName)
                        AboutRow(label = stringResource(R.string.about_description), value = stringResource(R.string.about_description_value))
                        AboutRow(label = stringResource(R.string.about_developed_at), value = stringResource(R.string.about_developed_at_value))
                        AboutRow(label = stringResource(R.string.about_semester), value = stringResource(R.string.about_semester_value))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun getLanguageDisplayName(language: AppLanguage): String {
    return when (language) {
        AppLanguage.SYSTEM -> stringResource(R.string.language_system)
        AppLanguage.GERMAN -> stringResource(R.string.lang_german)
        AppLanguage.ENGLISH -> stringResource(R.string.lang_english)
        AppLanguage.FRENCH -> stringResource(R.string.lang_french)
        AppLanguage.SPANISH -> stringResource(R.string.lang_spanish)
        AppLanguage.ITALIAN -> stringResource(R.string.lang_italian)
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailingAction: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                trailingAction?.invoke()
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(110.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
