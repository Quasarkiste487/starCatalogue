package com.example.starccatalogue.ui.stars

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.starccatalogue.network.Simbad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StarUiState(
    val id: String = "",
    val magnitude: String = "",
    val ra: String = "",
    val dec: String = "",
    val isLoading: Boolean = true
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StarsViewModel(private val starId: String): ViewModel(){
    private val _starState: MutableStateFlow<StarUiState> = MutableStateFlow(StarUiState())
    val starState: StateFlow<StarUiState> = _starState.asStateFlow()

    init {
        loadData()
    }

    companion object {
        fun provideFactory(starId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StarsViewModel(starId) as T
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val query = """
                SELECT TOP 1 id, V as mag, ra, dec from ident
                JOIN allfluxes USING(oidref)
                JOIN basic on oid = oidref
                WHERE id = '$starId'
                ORDER BY V
            """.trimIndent()

            val starsResponse = Simbad().fetchData(query)

            _starState.update {
                if(starsResponse != null && starsResponse.error() == null) {
                    val table = starsResponse.buildStarTable()
                    if (table.rowCount > 0) {
                        val row = table.getRow(0)
                        StarUiState(
                            id = row[0]?.toString() ?: "",
                            magnitude = row[1]?.toString() ?: "",
                            ra = row[2]?.toString() ?: "",
                            dec = row[3]?.toString() ?: "",
                            isLoading = false
                        )
                    } else {
                        StarUiState(id = starId, isLoading = false)
                    }
                } else {
                    StarUiState(id = starId, isLoading = false)
                }
            }
        }
    }
}