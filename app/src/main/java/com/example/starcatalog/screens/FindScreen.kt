package com.example.starcatalog.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.starcatalog.data.StarRepository
import com.example.starcatalog.model.StarData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindScreen() {
    var text by remember { mutableStateOf("") }
    var stars by remember { mutableStateOf(emptyList<StarData>()) }
    val repository = remember { StarRepository() }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                scope.launch {
                    stars = repository.getStars(newText)
                }
            },
            label = { Text("Search Stars") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(stars) { star ->
                Text(
                    text = "${star.name} (${star.constellation})",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
