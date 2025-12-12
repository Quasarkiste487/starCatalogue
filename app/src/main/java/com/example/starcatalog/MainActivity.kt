package com.example.starcatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.starcatalog.screens.FindScreen
import com.example.starcatalog.screens.HomeScreen
import com.example.starcatalog.screens.OptionsScreen
import com.example.starcatalog.screens.Screen
import com.example.starcatalog.screens.VisitedScreen
import com.example.starcatalog.ui.theme.StarCatalogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StarCatalogTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StarCatalogApp()
                }
            }
        }
    }
}

@Composable
fun StarCatalogApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Options.route) {
            OptionsScreen()
        }
        composable(Screen.Find.route) {
            FindScreen()
        }
        composable(Screen.Visited.route) {
            VisitedScreen()
        }
    }
}
