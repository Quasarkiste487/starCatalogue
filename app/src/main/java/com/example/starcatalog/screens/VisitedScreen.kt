package com.example.starcatalog.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.starcatalog.data.StarRepository
import com.example.starcatalog.model.StarData

@Composable
fun VisitedScreen() {
    var visitedStars by remember { mutableStateOf(emptyList<StarData>()) }
    val repository = remember { StarRepository() }

    LaunchedEffect(Unit) {
        visitedStars = repository.getVisitedStars()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Visited Stars", modifier = Modifier.padding(bottom = 16.dp))
        
        LazyColumn {
            items(visitedStars) { star ->
                Text(
                    text = "${star.name} - ${star.distanceLightYears} ly",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
