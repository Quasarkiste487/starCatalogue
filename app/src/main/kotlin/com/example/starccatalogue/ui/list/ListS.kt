package com.example.starccatalogue.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ListS (
    onStarClick: (String) -> Unit,
    onUpClick: () -> Unit,
    viewModel: ListVM = viewModel(),
) {
    val stars by viewModel.stars.collectAsStateWithLifecycle()
    ListS(
        stars = stars,
        onUpClick = onUpClick,
        onStarClick = onStarClick,
    )

}

@Composable
private fun ListS(
    stars: List<StarItem>,
    onUpClick: () -> Unit,
    onStarClick: (String) -> Unit,
) {
   // Implement the UI for displaying the list of stars
}