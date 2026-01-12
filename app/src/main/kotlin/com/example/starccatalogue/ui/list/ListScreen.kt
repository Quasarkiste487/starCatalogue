package com.example.starccatalogue.ui.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ListScreen(
    onNavigateBack: () -> Unit,
    onStarClick: (String) -> Unit,
    viewModel: ListViewModel = ListViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ListScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onStarClick = onStarClick,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onMagnitudeFilterChange = viewModel::updateMagnitudeFilter,
        onStarTypeFilterChange = viewModel::updateStarTypeFilter
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScreen(
    uiState: ListUiState,
    onNavigateBack: () -> Unit,
    onStarClick: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onMagnitudeFilterChange: (MagnitudeFilter) -> Unit,
    onStarTypeFilterChange: (StarTypeFilter) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    SearchBarSection(
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Suchen",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Filters section
            FiltersSection(
                magnitudeFilter = uiState.magnitudeFilter,
                starTypeFilter = uiState.starTypeFilter,
                onMagnitudeFilterChange = onMagnitudeFilterChange,
                onStarTypeFilterChange = onStarTypeFilterChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Content section
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            color = Color(0xFF1A237E)
                        )
                    }
                    uiState.errorMessage != null -> {
                        EmptyStateMessage(
                            message = uiState.errorMessage,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.stars.isEmpty() && uiState.searchQuery.isBlank() -> {
                        EmptyStateMessage(
                            message = "Geben Sie einen Suchbegriff ein",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.stars.isNotEmpty() -> {
                        StarsListSection(
                            stars = uiState.stars,
                            onStarClick = onStarClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBarSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
            Text(
                "Stern suchen...",
                color = Color.White.copy(alpha = 0.6f)
            )
        },
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
private fun FiltersSection(
    magnitudeFilter: MagnitudeFilter,
    starTypeFilter: StarTypeFilter,
    onMagnitudeFilterChange: (MagnitudeFilter) -> Unit,
    onStarTypeFilterChange: (StarTypeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Helligkeit",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(MagnitudeFilter.entries) { filter ->
                FilterChip(
                    selected = magnitudeFilter == filter,
                    onClick = { onMagnitudeFilterChange(filter) },
                    label = { Text(filter.displayName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Objekttyp",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(StarTypeFilter.entries) { filter ->
                FilterChip(
                    selected = starTypeFilter == filter,
                    onClick = { onStarTypeFilterChange(filter) },
                    label = { Text(filter.displayName) }
                )
            }
        }
    }
}

@Composable
private fun StarsListSection(
    stars: List<StarListItem>,
    onStarClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(stars) { star ->
            StarListItemCard(
                star = star,
                onClick = { onStarClick(star.id) }
            )
        }
    }
}

@Composable
private fun StarListItemCard(
    star: StarListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE6EAF1)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFF1A237E),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = star.id,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = "Magnitude: ${star.magnitude}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF625b71)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ListScreenPreview() {
    MaterialTheme {
        ListScreen(
            uiState = ListUiState(
                searchQuery = "Sirius",
                stars = listOf(
                    StarListItem(id = "Sirius", magnitude = "-1.46"),
                    StarListItem(id = "Betelgeuse", magnitude = "0.42"),
                    StarListItem(id = "Rigel", magnitude = "0.13"),
                    StarListItem(id = "Aldebaran", magnitude = "0.85"),
                    StarListItem(id = "Antares", magnitude = "1.09")
                ),
                isLoading = false,
                magnitudeFilter = MagnitudeFilter.ALL,
                starTypeFilter = StarTypeFilter.ALL,
                errorMessage = null
            ),
            onNavigateBack = {},
            onStarClick = {},
            onSearchQueryChange = {},
            onMagnitudeFilterChange = {},
            onStarTypeFilterChange = {}
        )
    }
}

