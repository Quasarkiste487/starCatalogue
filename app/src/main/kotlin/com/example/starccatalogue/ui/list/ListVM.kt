package com.example.starccatalogue.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.SimbadSQLSource
import com.example.starccatalogue.network.StarDataSource
import com.example.starccatalogue.network.StarOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListVM(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val starName: String = savedStateHandle
        .toRoute<ListR>()
        .starName
    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow(starName)
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _stars: MutableStateFlow<List<StarOverview>> = MutableStateFlow(emptyList())
    val stars: StateFlow<List<StarOverview>> = _stars.asStateFlow()
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val repo: StarDataSource = SimbadSQLSource(simbad = Simbad())

    init {
        loadData(starName)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun search() {
        loadData(_searchQuery.value)
    }

    private fun loadData(query: String) {
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.update { true }
            _error.update { null }
            _stars.update { emptyList() }
            try {
                val starList = repo.listStars(10, query)
                _stars.update {
                    starList
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("UnknownHost", ignoreCase = true) == true ||
                    e.message?.contains("Network", ignoreCase = true) == true -> 
                        "Netzwerkverbindung fehlgeschlagen. Bitte überprüfen Sie Ihre Internetverbindung."
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Zeitüberschreitung. Bitte versuchen Sie es erneut."
                    else -> e.message ?: "Ein Fehler ist aufgetreten"
                }
                _error.update { errorMessage }
            } finally {
                _isLoading.update { false }
            }
        }
    }
}