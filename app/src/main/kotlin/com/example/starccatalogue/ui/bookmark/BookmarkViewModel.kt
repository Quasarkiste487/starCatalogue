package com.example.starccatalogue.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starccatalogue.network.StarOverview
import com.example.starccatalogue.util.Bookmarks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BookmarkViewModel(
    private val bookmarks: Bookmarks
): ViewModel() {
    private val _starlist: MutableStateFlow<List<StarOverview>> = MutableStateFlow(emptyList())
    val starlist: StateFlow<List<StarOverview>> = _starlist.asStateFlow()

    init {
        bookmarks.bookmarksFlow
            .onEach { starList -> _starlist.value = starList }
            .launchIn(viewModelScope)
    }
}
