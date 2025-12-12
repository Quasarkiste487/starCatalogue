package com.example.starcatalog.screens

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Options : Screen("options")
    object Find : Screen("find")
    object Visited : Screen("visited")
}
