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

// Datenklasse für den Blog-Artikel
data class BlogArticle(
    val date: String,
    val title: String,
    val paragraphs: List<String>
)

// Datenklasse für den Top-Stern
data class TopStar(
    val id: String, // Changed to String to match updated StarsRoute
    val name: String,
    val description: String
)

// UI-State für den HomeScreen
data class HomeUiState(
    val events: List<EventRow> = emptyList(),
    val blogArticle: BlogArticle? = null,
    val topStar: TopStar? = null
)

// ViewModel welchen Daten für HomeScreen bereitstellt
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val events = listOf(
            EventRow(
                title = "Sonnenfinsternis",
                subtitle = "Totale Finsternis in Europa sichtbar.",
                time = "Taggesamtzeit - 10:00"
            ),
            EventRow(
                title = "Mars Opposition",
                subtitle = "Mars steht der Sonne genau gegenüber.",
                time = "Stundenhalbzeit - 22:30"
            ),
            EventRow(
                title = "Meteorstrom",
                subtitle = "Die Perseiden erreichen ihr Maximum.",
                time = "Nacht - 02:00"
            )
        )

        val blogArticle = BlogArticle(
            date = "15.07.2024",
            title = "Aktuelle Beobachtungen",
            paragraphs = listOf(
                "In den kommenden Nächten lohnt sich ein Blick Richtung Süden. Der Skorpion steht hoch am Himmel und Antares funkelt rötlich.",
                "Auch der Sommernachtshimmel bietet mit dem Sommerdreieck gute Orientierungshilfen für Einsteiger."
            )
        )

        val topStar = TopStar(
            id = "Sirius",
            name = "Sirius",
            description = "Der hellste Stern am Nachthimmel."
        )

        _uiState.value = HomeUiState(
            events = events,
            blogArticle = blogArticle,
            topStar = topStar
        )
    }
}
