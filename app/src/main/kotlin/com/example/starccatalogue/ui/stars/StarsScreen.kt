package com.example.starccatalogue.ui.stars

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun StarsScreen(
    starId: String,
    onNavigateBack: () -> Unit,
    viewModel: StarsViewModel = StarsViewModel(starId),
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
                .padding(16.dp)
        ) {
            // Placeholder Image
            PlaceholderImage()

            Spacer(modifier = Modifier.height(24.dp))

            // Basisdaten Section
            if (starState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                BasisdatenSection(starState = starState)
            }
        }
    }
}

@Composable
private fun PlaceholderImage(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEDE7F6)),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder shapes similar to the design
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Triangle shape (top)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.TopCenter)
                    .clip(CutCornerShape(topStart = 25.dp, topEnd = 25.dp))
                    .background(Color(0xFFBDBDBD))
            )
            // Star shape (bottom left) - using circle as placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .background(Color(0xFFBDBDBD))
            )
            // Rounded rectangle (bottom right)
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFBDBDBD))
            )
        }
    }
}

@Composable
private fun BasisdatenSection(
    starState: StarUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Basisdaten:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = "ID: ${starState.id}")
        Text(text = "Magnitude: ${starState.magnitude}")
        Text(text = "RA: ${starState.ra}")
        Text(text = "Dec: ${starState.dec}")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StarsScreenPreview() {
    StarsScreen(
        starState = StarUiState(
            id = "Stern xy",
            magnitude = "1.46",
            ra = "101.2875",
            dec = "-16.7161",
            isLoading = false
        ),
        onNavigateBack = {}
    )
}
