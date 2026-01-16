package com.example.starccatalogue.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StarItem(
    val name: String,
    val type: String,
)
class ListVM(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val starName: String = savedStateHandle
        .toRoute<ListR>()
        .starName
    val searchQuery: String
        get() = starName
    private val _stars: MutableStateFlow<List<StarItem>> = MutableStateFlow(emptyList())
    val stars: StateFlow<List<StarItem>> = _stars.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // TODO: Replace with real data loading logic and query with starName
            val starsResponse = listOf(
                StarItem(name = "Sirius", type = "typ 1"),
                StarItem(name = "Canopus", type = "typ 2"),
                StarItem(name = "Arcturus", type = "typ 2"),
                StarItem(name = "Vega", type = "typ 2"),
                StarItem(name = "Capella", type = "typ 4"),
            )
            _stars.update {
                if (starsResponse != null) {
                    starsResponse
                } else {
                    emptyList()
                }
            }
        }
    }
}