package com.example.starccatalogue.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.starccatalogue.ui.bookmark.BookmarkRoute
import com.example.starccatalogue.ui.bookmark.BookmarkScreen
import com.example.starccatalogue.ui.home.HomeRoute
import com.example.starccatalogue.ui.home.HomeScreen
import com.example.starccatalogue.ui.list.ListR
import com.example.starccatalogue.ui.list.ListS
import com.example.starccatalogue.ui.settings.SettingsRoute
import com.example.starccatalogue.ui.settings.SettingsScreen
import com.example.starccatalogue.ui.stars.StarsRoute
import com.example.starccatalogue.ui.stars.StarsScreen
import com.example.starccatalogue.ui.theme.StarcCatalogueTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { StarcCatalogueTheme { AppRoot() } }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<HomeRoute>() } == true,
                    onClick = {
                        navController.navigate(HomeRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.List, contentDescription = "Verzeichnis"
                        )
                    },
                    label = { Text("Verzeichnis") },
                    selected = currentDestination?.hierarchy?.any {
                        it.hasRoute<ListR>()
                    } == true,
                    onClick = {
                        navController.navigate(ListR("")) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.BookmarkBorder, contentDescription = "Lesezeichen"
                        )
                    },
                    label = { Text("Lesezeichen") },
                    selected = currentDestination?.hierarchy?.any {
                        it.hasRoute<BookmarkRoute>()
                    } == true,
                    onClick = {
                        navController.navigate(BookmarkRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Settings, contentDescription = "Einstellungen"
                        )
                    },
                    label = { Text("Einstellungen") },
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<SettingsRoute>() } == true,
                    onClick = {
                        navController.navigate(SettingsRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }
        }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier.padding(paddingValues),
        ) {
            composable<HomeRoute> {
                HomeScreen(onSearch = { starName ->
                    navController.navigate(ListR(starName)) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }, onProfileClick = { starId ->
                    navController.navigate(StarsRoute(starId))
                })
            }
            composable<ListR> {
                ListS(onStarClick = { starId ->
                    navController.navigate(StarsRoute(starId))
                })
            }
            composable<StarsRoute> {
                StarsScreen(
                    onNavigateBack = { navController.popBackStack() })
            }
            composable<BookmarkRoute> {
                BookmarkScreen(onStarClick = { star ->
                    navController.navigate(StarsRoute(star))
                })
            }
            composable<SettingsRoute> {
                SettingsScreen()
            }
        }
    }
}
