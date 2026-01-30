package com.example.starccatalogue.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.starccatalogue.network.Filter
import com.example.starccatalogue.network.Ordering
import com.example.starccatalogue.network.StarDataSource
import com.example.starccatalogue.network.StarOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListVM(
    savedStateHandle: SavedStateHandle, private val starSource: StarDataSource
) : ViewModel() {
    private val starName: String = savedStateHandle.toRoute<ListR>().starName
    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow(starName)
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _stars: MutableStateFlow<List<StarOverview>> = MutableStateFlow(emptyList())
    val stars: StateFlow<List<StarOverview>> = _stars.asStateFlow()

    init {
        loadData(starName)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun search(query: String? = null) {
        loadData(query ?: _searchQuery.value)
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
