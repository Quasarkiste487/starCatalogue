package com.example.starcatalog.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Star Catalog", modifier = Modifier.padding(bottom = 32.dp))

        Button(
            onClick = { navController.navigate(Screen.Options.route) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Options")
        }

        Button(
            onClick = { navController.navigate(Screen.Find.route) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Find")
        }

        Button(
            onClick = { navController.navigate(Screen.Visited.route) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Visited")
        }
    }
}
