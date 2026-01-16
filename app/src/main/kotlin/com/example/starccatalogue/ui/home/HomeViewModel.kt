package com.example.starccatalogue.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Datenklasse für ein Event – wird auch im UI verwendet
data class EventRow(
    val title: String,
    val subtitle: String,
    val time: String
)

// UI-State für den HomeScreen
data class HomeUiState(
    val events: List<EventRow> = emptyList()
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // TODO: stern des tages laden, blog artikel und nächste events
        // vorerst Dummy-Daten für Events
        val events = listOf(
            EventRow(
                title = "Sonnenfinsternis",
                subtitle = "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                time = "Taggesamtzeit - 10:00"
            ),
            EventRow(
                title = "Mars und Saturn stehen im Zwiespalt",
                subtitle = "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                time = "Stundenhalbzeit - 10:30"
            ),
            EventRow(
                title = "Pluto wird wieder Planet",
                subtitle = "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                time = "Normalzeit - 13:37"
            )
        )
        _uiState.value = HomeUiState(events = events)
    }
}
