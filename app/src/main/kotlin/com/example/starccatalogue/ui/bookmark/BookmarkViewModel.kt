package com.example.starccatalogue.ui.bookmark

import com.example.starccatalogue.ui.list.ListR

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.starccatalogue.network.Filter
import com.example.starccatalogue.network.Ordering
import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.SimbadSQLSource
import com.example.starccatalogue.network.StarDataSource
import com.example.starccatalogue.network.StarOverview
import com.example.starccatalogue.util.Bookmarks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookmarkViewModel(
    private val bookmarks: Bookmarks
): ViewModel() {
    private val _stars: MutableStateFlow<List<StarOverview>> = MutableStateFlow(emptyList())
    val stars: StateFlow<List<StarOverview>> = _stars.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO){
            val starList = bookmarks.getBookmarks()
            _stars.update {
                starList
            }
        }
    }
}
