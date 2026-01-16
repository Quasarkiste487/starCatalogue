package com.example.starccatalogue.ui.stars

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun StarsScreen(
    starId: String,
    onNavigateBack: () -> Unit,
    viewModel: StarsViewModel = viewModel(factory = StarsViewModel.provideFactory(starId)),
) {
    val starState by viewModel.starState.collectAsStateWithLifecycle()
    StarsScreen(
        starState = starState,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StarsScreen(
    starState: StarUiState,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(starState.id.ifEmpty { "Stern xy" }) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Bookmark-Funktion */ }) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = "Merken"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section mit Gradient
            HeroSection(
                starState = starState,
                isLoading = starState.isLoading
            )

            // Data Cards Section
            AnimatedVisibility(
                visible = !starState.isLoading,
                enter = fadeIn()
            ) {
                DataCardsSection(starState = starState)
            }
        }
    }
}

@Composable
private fun HeroSection(
    starState: StarUiState,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E), // Indigo 900
                        Color(0xFF4A148C)  // Deep Purple 900
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(48.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107), // Amber
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = starState.id.ifEmpty { "Stern" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                if (starState.magnitude.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Magnitude ${starState.magnitude}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFE1BEE7), // Light purple
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DataCardsSection(
    starState: StarUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Basisdaten",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // ID Card
        if (starState.id.isNotEmpty()) {
            DataCard(
                label = "Identifier",
                value = starState.id,
                icon = Icons.Outlined.LightMode
            )
        }

        // Magnitude & RA Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (starState.magnitude.isNotEmpty()) {
                DataCard(
                    label = "Magnitude",
                    value = starState.magnitude,
                    icon = Icons.Filled.Star,
                    modifier = Modifier.weight(1f)
                )
            }
            if (starState.ra.isNotEmpty()) {
                DataCard(
                    label = "RA",
                    value = starState.ra,
                    icon = Icons.Outlined.MyLocation,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Dec Card
        if (starState.dec.isNotEmpty()) {
            DataCard(
                label = "Declination",
                value = starState.dec,
                icon = Icons.Outlined.Explore
            )
        }
    }
}

@Composable
private fun DataCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StarsScreenPreview() {
    StarsScreen(
        starState = StarUiState(
            id = "Sirius",
            magnitude = "-1.46",
            ra = "101.287155",
            dec = "-16.716116",
            isLoading = false
        ),
        onNavigateBack = {}
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Loading State")
@Composable
private fun StarsScreenLoadingPreview() {
    StarsScreen(
        starState = StarUiState(
            id = "",
            magnitude = "",
            ra = "",
            dec = "",
            isLoading = true
        ),
        onNavigateBack = {}
    )
}

