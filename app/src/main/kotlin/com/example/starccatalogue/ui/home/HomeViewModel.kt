package com.example.starccatalogue.ui.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.starccatalogue.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Datenklasse für ein Event – mit String Resource IDs
data class EventRow(
    @param:StringRes val titleRes: Int,
    @param:StringRes val subtitleRes: Int,
    @param:StringRes val timeRes: Int
)

// Datenklasse für den Blog-Artikel - mit String Resource IDs
data class BlogArticle(
    val date: String,
    @param:StringRes val titleRes: Int,
    val paragraphRes: List<Int>
)

// Datenklasse für den Top-Stern
data class TopStar(
    val id: Int,
    val name: String,
    @param:StringRes val descriptionRes: Int
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
        val events = listOf(
            EventRow(
                titleRes = R.string.event_1_title,
                subtitleRes = R.string.event_1_subtitle,
                timeRes = R.string.event_1_time
            ),
            EventRow(
                titleRes = R.string.event_2_title,
                subtitleRes = R.string.event_2_subtitle,
                timeRes = R.string.event_2_time
            ),
            EventRow(
                titleRes = R.string.event_3_title,
                subtitleRes = R.string.event_3_subtitle,
                timeRes = R.string.event_3_time
            )
        )

        val blogArticle = BlogArticle(
            date = "15.07.2024",
            titleRes = R.string.blog_title,
            paragraphRes = listOf(
                R.string.blog_paragraph_1,
                R.string.blog_paragraph_2
            )
        )

        val topStar = TopStar(
            id = 8399845, // Sirius
            name = "Sirius",
            descriptionRes = R.string.top_star_description
        )

        _uiState.value = HomeUiState(
            events = events,
            blogArticle = blogArticle,
            topStar = topStar
        )
    }
}
