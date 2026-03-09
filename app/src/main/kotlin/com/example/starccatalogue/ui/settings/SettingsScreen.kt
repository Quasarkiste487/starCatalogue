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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.starccatalogue.util.AppLanguage
import com.example.starccatalogue.util.ThemeMode
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.settingsState.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null) },
            title = { Text("Lesezeichen zurücksetzen") },
            text = { Text("Alle Lesezeichen werden unwiderruflich gelöscht. Fortfahren?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearBookmarks()
                    showClearDialog = false
                }) { Text("Löschen", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Abbrechen") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Einstellungen") })
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
            SettingsSection(title = "Erscheinungsbild", icon = Icons.Default.Palette) {
                Text(
                    text = "Farbschema",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val modes = listOf(
                        ThemeMode.LIGHT to "Hell",
                        ThemeMode.SYSTEM to "System",
                        ThemeMode.DARK  to "Dunkel"
                    )
                    modes.forEachIndexed { index, (mode, label) ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                            onClick = { viewModel.setThemeMode(mode) },
                            selected = state.themeMode == mode,
                            label = { Text(label) },
                            icon = {
                                val icon = when (mode) {
                                    ThemeMode.LIGHT  -> Icons.Default.LightMode
                                    ThemeMode.DARK   -> Icons.Default.DarkMode
                                    ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                }
                                SegmentedButtonDefaults.Icon(active = state.themeMode == mode) {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(SegmentedButtonDefaults.IconSize))
                                }
                            }
                        )
                    }
                }
            }

            // ── Sprache ──────────────────────────────────────────────────────
            SettingsSection(title = "Sprache", icon = Icons.Default.Language) {
                ExposedDropdownMenuBox(
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.language.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sprache auswählen") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false }
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang.displayName) },
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
            SettingsSection(title = "Lesezeichen", icon = Icons.Default.Bookmark) {
                OutlinedButton(
                    onClick = { showClearDialog = true },
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
                    Text("Lesezeichen zurücksetzen")
                }
            }

            // ── Über die App ─────────────────────────────────────────────────
            SettingsSection(
                title = "Über die App",
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
                        AboutRow(label = "App", value = "Star Catalogue")
                        AboutRow(label = "Version", value = "1.0.0")
                        AboutRow(
                            label = "Beschreibung",
                            value = "Durchsuche und erkunde Sterne aus dem SIMBAD-Katalog. " +
                                    "Speichere Favoriten als Lesezeichen und passe die App nach deinen Wünschen an."
                        )
                        AboutRow(label = "Entwickelt an", value = "HTWK Leipzig")
                        AboutRow(label = "Semester", value = "5. Semester – Mobile Computing")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
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
