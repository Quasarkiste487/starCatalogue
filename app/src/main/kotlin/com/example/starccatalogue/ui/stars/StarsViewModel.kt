package com.example.starccatalogue.ui.stars

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.SimbadSQLSource
import com.example.starccatalogue.network.StarDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StarUiState(
    val name: String = "",
    val id: Int = -1,
    val magnitude: Float = 0.0f,
    val ra: Float = 0.0f,
    val dec: Float = 0.0f,
    val isLoading: Boolean = true
)

class StarsViewModel(
    private val starSource: StarDataSource,
    savedStateHandle: SavedStateHandle,
): ViewModel(){

    private val starId: Int = savedStateHandle.toRoute<StarsRoute>().starId
    private val _starState: MutableStateFlow<StarUiState> = MutableStateFlow(StarUiState())
    val starState: StateFlow<StarUiState> = _starState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val star = starSource.getStarDetails(starId)
            _starState.update {
                if(star != null) {
                        StarUiState(
                            id = star.oid,
                            magnitude = star.mag,
                            ra = star.ra,
                            dec = star.dec,
                            isLoading = false,
                            name = star.name,
                        )
                } else {
                    StarUiState(id = starId, isLoading = false)
                }
            }
        }
    }
}
