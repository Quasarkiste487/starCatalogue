package com.example.starccatalogue.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.starccatalogue.ui.bookmark.BookmarkRoute
import com.example.starccatalogue.ui.bookmark.BookmarkScreen
import com.example.starccatalogue.ui.drawer.AppDrawer
import com.example.starccatalogue.ui.home.HomeRoute
import com.example.starccatalogue.ui.home.HomeScreen
import com.example.starccatalogue.ui.list.ListR
import com.example.starccatalogue.ui.list.ListS
import com.example.starccatalogue.ui.stars.StarsRoute
import com.example.starccatalogue.ui.stars.StarsScreen
import com.example.starccatalogue.ui.theme.StarcCatalogueTheme
import kotlinx.coroutines.launch

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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            AppDrawer(
                appName = "Star Catalogue",
                onHomeClick = {
                    navController.navigate(HomeRoute)
                    scope.launch { drawerState.close() }
                },
                onStarListClick = {
                    navController.navigate(ListR(""))
                    scope.launch { drawerState.close() }
                },
                onBookmarkClick = {
                    navController.navigate(BookmarkRoute)
                    scope.launch { drawerState.close() }
                }
            )
        }) {
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    onSearch = { starName ->
                        navController.navigate(ListR(starName))
                    },
                    onProfileClick = { starId ->
                        navController.navigate(StarsRoute(starId))
                    }
                )
            }
            composable<ListR> {
                ListS(onUpClick = { navController.popBackStack() },
                    onStarClick = { star ->
                    navController.navigate(StarsRoute(star.toString()))
                })
            }
            composable<StarsRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<StarsRoute>()
                StarsScreen(
                    starId = route.starId,
                    onNavigateBack = { navController.popBackStack() })
            }
            composable<BookmarkRoute> {
                BookmarkScreen(
                    onUpClick = { navController.popBackStack()},
                    onStarClick = { star ->
                        navController.navigate(StarsRoute(star))
                    }
                )
            }
        }
    }
}
