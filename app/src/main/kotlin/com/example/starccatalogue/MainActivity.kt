package com.example.starccatalogue

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { StarcCatalogueTheme { AppRoot() } }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                    navController.navigate(ListRoute)
                    scope.launch { drawerState.close() }
                },
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
                    })
            }
            composable<ListRoute> {
                ListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onStarClick = {}
                )
            }
            composable<StarsRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<StarsRoute>()
                StarsScreen(
                    starId = route.starId, onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppDrawer(
    appName: String, onHomeClick: () -> Unit, onStarListClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.8f) // Drawer-Breite
            .background(Color.White)
    ) {
        // Banner oben mit App-Namen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF7970FF))
                .padding(24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = appName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menüeinträge
        NavigationDrawerItem(
            label = { Text("Home") },
            selected = false,
            onClick = onHomeClick,
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFE6EAF1)
            )
        )

        NavigationDrawerItem(
            label = { Text("Sternenliste") },
            selected = false,
            onClick = onStarListClick,
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFE6EAF1)
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // App-Infos unten
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Version 1.0.0", // feste Version oder später per Parameter
                style = MaterialTheme.typography.bodySmall, color = Color.Gray
            )
            Text(
                text = "© 2026 Star Catalogue",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
