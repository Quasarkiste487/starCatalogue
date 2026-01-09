package com.example.starccatalogue.ui.stars

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.starccatalogue.network.QueryScript
import com.example.starccatalogue.network.Simbad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.starlink.table.EmptyStarTable
import uk.ac.starlink.table.StarTable

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StarsViewModel: ViewModel(){
    private val _stars: MutableStateFlow<StarTable> = MutableStateFlow(EmptyStarTable())
    val stars: StateFlow<StarTable> = _stars.asStateFlow()

    init {
        loadData()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _stars.update {
                //val script = QueryScript(10, listOf("main_id", "coordinates", "flux(V)"), "Vmag < 6")
                val query = """
                SELECT TOP 100 id, V as mag, ra, dec  from ident
                JOIN allfluxes USING(oidref)
                JOIN basic on oid = oidref
                WHERE id LIKE '%NAME%'
                ORDER BY V
                """.trimIndent()

                val starsResponse = Simbad().fetchData(query)

                if(starsResponse != null && starsResponse.error() == null) {
                    starsResponse.buildStarTable()
                } else {
                    EmptyStarTable()
                }
            }
        }
    }
}