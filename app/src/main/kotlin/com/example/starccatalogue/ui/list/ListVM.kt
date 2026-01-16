package com.example.starccatalogue.ui.list

import android.util.Log
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
    val searchQuery: String
        get() = starName
    private val _stars: MutableStateFlow<List<StarOverview>> = MutableStateFlow(emptyList())
    val stars: StateFlow<List<StarOverview>> = _stars.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO){
            val repo : StarDataSource = SimbadSQLSource(simbad = Simbad())
            Log.w("ListVM", "repo initialized, loading stars")
            val starList = repo.listStars(10)
            Log.w("ListVM", "loaded $starList")
            _stars.update {
                starList
            }
            Log.w("ListVM", "updated stars to $starList")
        }
    }
}