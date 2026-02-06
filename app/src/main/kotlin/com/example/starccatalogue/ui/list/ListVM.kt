package com.example.starccatalogue.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starccatalogue.network.Filter
import com.example.starccatalogue.network.Ordering
import com.example.starccatalogue.network.StarDataSource
import com.example.starccatalogue.network.StarOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListVM(
    savedStateHandle: SavedStateHandle, private val starSource: StarDataSource
) : ViewModel() {
    private val starNameFlow = savedStateHandle.getStateFlow("starName", "")
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = combine(starNameFlow, _searchQuery) { fromRoute, manual ->
        manual.ifEmpty { fromRoute }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), starNameFlow.value)

    private val _stars: MutableStateFlow<List<StarOverview>> = MutableStateFlow(emptyList())
    val stars: StateFlow<List<StarOverview>> = _stars.asStateFlow()

    init {
        viewModelScope.launch {
            searchQuery.collect { query ->
                loadData(query)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun search(query: String? = null) {
        val q = query ?: _searchQuery.value
        loadData(q)
    }

    private fun loadData(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val starList = starSource.listStarsRequest().limit(10)
                .filter("ident", Filter.like("id", "NAME %$query%"))
                .order(Ordering("allfluxes", "V")).fetch()
            _stars.update {
                starList
            }
        }
    }
}
