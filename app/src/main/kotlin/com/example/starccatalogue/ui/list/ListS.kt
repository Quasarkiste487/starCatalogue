package com.example.starccatalogue.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.starccatalogue.network.StarOverview
import com.example.starccatalogue.ui.theme.StarcCatalogueTheme

@Composable
fun ListS (
    onStarClick: (Int) -> Unit,
    onUpClick: () -> Unit,
    viewModel: ListVM = viewModel(),
) {
    val stars by viewModel.stars.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    ListS(
        stars = stars,
        searchQuery = searchQuery,
        isLoading = isLoading,
        error = error,
        onUpClick = onUpClick,
        onStarClick = onStarClick,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onSearch = viewModel::search,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListS(
    stars: List<StarOverview>,
    searchQuery: String,
    isLoading: Boolean,
    error: String?,
    onUpClick: () -> Unit,
    onStarClick: (Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
        CenterAlignedTopAppBar(
            navigationIcon = {
                IconButton(onClick = onUpClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "",
                    )
                }
            },
            title = {
                SearchTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    onSearch = onSearch,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentDescription = "Daten werden geladen"
                    )
                }
                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Fehler",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(stars) { star ->
                Card(
                    onClick = { onStarClick(star.oid) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 6.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107), // Amber color for star
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = star.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = star.typ,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.alpha(0.8f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(24.dp)
                                .alpha(0.6f)
                        )
                    }
                }
            }
                }
            }
        }
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text("Suchergebnisse") },
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ListScreenPreview() {
    StarcCatalogueTheme {
        ListS(
            stars = listOf(
                StarOverview(1, "Sirius", "Main Sequence"),
                StarOverview(2, "Canopus", "Supergiant"),
                StarOverview(3, "Arcturus", "Giant"),
            ),
            searchQuery = "Sirius",
            isLoading = false,
            error = null,
            onUpClick = {},
            onStarClick = {},
            onSearchQueryChange = {},
            onSearch = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ListScreenLoadingPreview() {
    StarcCatalogueTheme {
        ListS(
            stars = emptyList(),
            searchQuery = "Sirius",
            isLoading = true,
            error = null,
            onUpClick = {},
            onStarClick = {},
            onSearchQueryChange = {},
            onSearch = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ListScreenErrorPreview() {
    StarcCatalogueTheme {
        ListS(
            stars = emptyList(),
            searchQuery = "Sirius",
            isLoading = false,
            error = "Netzwerkverbindung fehlgeschlagen. Bitte überprüfen Sie Ihre Internetverbindung.",
            onUpClick = {},
            onStarClick = {},
            onSearchQueryChange = {},
            onSearch = {},
        )
    }
}