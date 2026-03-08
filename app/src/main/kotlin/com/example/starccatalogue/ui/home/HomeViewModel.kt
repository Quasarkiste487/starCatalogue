package com.example.starccatalogue.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Datenklasse für ein Event – wird auch im UI verwendet
data class EventRow(
    val title: String, val subtitle: String, val time: String
)

// Datenklasse für den Blog-Artikel
data class BlogArticle(
    val date: String, val title: String, val paragraphs: List<String>
)

// Datenklasse für den Top-Stern
data class TopStar(
    val id: Int, val name: String, val description: String
)

// UI-State für den HomeScreen
data class HomeUiState(
    val events: List<EventRow> = emptyList(),
    val blogArticle: BlogArticle? = null,
    val topStar: TopStar? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // TODO: stern des tages laden, blog artikel und nächste events
        val events = listOf(
            EventRow(
                title = "Sonnenfinsternis",
                subtitle = "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                time = "Taggesamtzeit - 10:00"
            ), EventRow(
                title = "Mars und Saturn stehen im Zwiespalt",
                subtitle = "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                time = "Stundenhalbzeit - 10:30"
            ), EventRow(
                title = "Pluto wird wieder Planet",
                subtitle = "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                time = "Normalzeit - 13:37"
            )
        )

        val blogArticle = BlogArticle(
            date = "15.07.2024", title = "Aktuelle Beobachtungen", paragraphs = listOf(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            )
        )

        val topStar = TopStar(
            id = 8399845, // Sirius
            name = "Sirius", description = "so schön ja"
        )

        _uiState.value = HomeUiState(
            events = events, blogArticle = blogArticle, topStar = topStar
        )
    }
}
