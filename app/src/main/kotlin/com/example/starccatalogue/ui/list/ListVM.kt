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
)
class ListVM(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val starName: String = savedStateHandle
        .toRoute<ListR>()
        .starName
    private val _stars: MutableStateFlow<List<StarItem>> = MutableStateFlow(emptyList())
    val stars: StateFlow<List<StarItem>> = _stars.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val starsResponse = listOf(
                StarItem(name = "Sirius"),
                StarItem(name = "Canopus"),
                StarItem(name = "Arcturus"),
                StarItem(name = "Vega"),
                StarItem(name = "Capella"),
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