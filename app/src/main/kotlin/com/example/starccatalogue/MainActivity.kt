package com.example.starccatalogue

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.starccatalogue.ui.home.HomeRoute
import com.example.starccatalogue.ui.home.HomeScreen
import com.example.starccatalogue.ui.list.ListRoute
import com.example.starccatalogue.ui.list.ListScreen
import com.example.starccatalogue.ui.stars.StarsRoute
import com.example.starccatalogue.ui.stars.StarsScreen
import com.example.starccatalogue.ui.theme.StarcCatalogueTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarcCatalogueTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable<ListRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<ListRoute>()
                        ListScreen(
                            initialSearchQuery = route.searchQuery,
                            onNavigateBack = { navController.popBackStack() },
                            onStarClick = { starId ->
                                navController.navigate(StarsRoute(starId = starId))
                            }
                        )
                    }
                    composable<HomeRoute> {
                        HomeScreen(
                            onProfileClick = { starId ->
                                navController.navigate(StarsRoute(starId = starId))
                            },
                            onSearch = { query ->
                                navController.navigate(ListRoute(searchQuery = query))
                            },
                            onOpenDrawer = {}
                        )
                    }
                    composable<StarsRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<StarsRoute>()
                        StarsScreen(
                            starId = route.starId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
