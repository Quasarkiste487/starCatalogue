package com.example.starccatalogue.ui.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starccatalogue.network.Simbad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StarListItem(
    val id: String,
    val magnitude: String
)

enum class MagnitudeFilter(val displayName: String, val range: Pair<Double, Double>?) {
    ALL("Alle", null),
    VERY_BRIGHT("Sehr hell (<3)", Pair(Double.NEGATIVE_INFINITY, 3.0)),
    BRIGHT("Hell (3-6)", Pair(3.0, 6.0)),
    MEDIUM("Mittel (6-9)", Pair(6.0, 9.0)),
    DIM("Schwach (>9)", Pair(9.0, Double.POSITIVE_INFINITY))
}

enum class StarTypeFilter(val displayName: String, val type: String?) {
    ALL("Alle", null),
    STAR("Stern", "Star"),
    GALAXY("Galaxie", "Galaxy"),
    NEBULA("Nebel", "Nebula")
}

data class ListUiState(
    val searchQuery: String = "",
    val stars: List<StarListItem> = emptyList(),
    val isLoading: Boolean = false,
    val magnitudeFilter: MagnitudeFilter = MagnitudeFilter.ALL,
    val starTypeFilter: StarTypeFilter = StarTypeFilter.ALL,
    val errorMessage: String? = null
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ListViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ListUiState> = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isNotBlank()) {
            searchStars()
        } else {
            _uiState.update { it.copy(stars = emptyList(), errorMessage = null) }
        }
    }

    fun updateMagnitudeFilter(filter: MagnitudeFilter) {
        _uiState.update { it.copy(magnitudeFilter = filter) }
        if (_uiState.value.searchQuery.isNotBlank()) {
            searchStars()
        }
    }

    fun updateStarTypeFilter(filter: StarTypeFilter) {
        _uiState.update { it.copy(starTypeFilter = filter) }
        if (_uiState.value.searchQuery.isNotBlank()) {
            searchStars()
        }
    }

    private fun searchStars() {
        val currentState = _uiState.value
        val query = currentState.searchQuery
        val magnitudeFilter = currentState.magnitudeFilter
        val starTypeFilter = currentState.starTypeFilter

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Build WHERE clause for magnitude filter
                val magnitudeCondition = magnitudeFilter.range?.let { (min, max) ->
                    if (min == Double.NEGATIVE_INFINITY) {
                        "AND V < $max"
                    } else if (max == Double.POSITIVE_INFINITY) {
                        "AND V > $min"
                    } else {
                        "AND V BETWEEN $min AND $max"
                    }
                } ?: ""

                // Build WHERE clause for star type filter
                val typeCondition = starTypeFilter.type?.let {
                    "AND otype_txt LIKE '%$it%'"
                } ?: ""

                val sqlQuery = """
                    SELECT TOP 25 id, V as mag
                    FROM ident
                    JOIN allfluxes USING(oidref)
                    JOIN basic ON oid = oidref
                    WHERE id LIKE '%$query%'
                    $magnitudeCondition
                    $typeCondition
                    ORDER BY V
                """.trimIndent()

                val response = Simbad().fetchData(sqlQuery)

                _uiState.update {
                    if (response != null && response.error() == null) {
                        val table = response.buildStarTable()
                        val starsList = mutableListOf<StarListItem>()

                        for (i in 0 until table.rowCount) {
                            val row = table.getRow(i)
                            starsList.add(
                                StarListItem(
                                    id = row[0]?.toString() ?: "",
                                    magnitude = row[1]?.toString() ?: "N/A"
                                )
                            )
                        }

                        it.copy(
                            stars = starsList,
                            isLoading = false,
                            errorMessage = if (starsList.isEmpty()) "Keine Ergebnisse gefunden" else null
                        )
                    } else {
                        it.copy(
                            stars = emptyList(),
                            isLoading = false,
                            errorMessage = response?.error() ?: "Fehler beim Laden der Daten"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        stars = emptyList(),
                        isLoading = false,
                        errorMessage = "Fehler: ${e.message}"
                    )
                }
            }
        }
    }
}

