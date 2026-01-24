package com.example.starccatalogue.ui.stars

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.starccatalogue.network.Simbad
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StarUiState(
    val name: String = "",
    val scientificId: String = "",
    val magnitude: String = "",
    val ra: String = "",
    val dec: String = "",
    val spectralClass: String = "",
    val distanceLightYears: String = "",
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
            // STEP 1: Try to find the star's OID and basic data using the user's search term.
            // This resembles the logic that successfully returned data before.
            val queryStep1 = """
                SELECT TOP 1 basic.oid, basic.main_id, basic.ra, basic.dec, allfluxes.V, basic.plx_value, basic.sp_type
                FROM basic
                JOIN ident ON ident.oidref = basic.oid
                LEFT JOIN allfluxes ON allfluxes.oidref = basic.oid
                WHERE ident.id = '$starId' OR ident.id = 'NAME $starId' OR ident.id = '* $starId'
            """.trimIndent()

            val response1 = Simbad().fetchData(queryStep1)
            val table1 = response1?.buildStarTable() // Changed var to val as per warning

            if (table1 != null && table1.rowCount > 0L) { // Added 'L' to 0
                val row = table1.getRow(0)
                val oid = row[0]?.toString() ?: ""
                val mainIdRaw = row[1]?.toString() ?: ""
                val raRaw = row[2]?.toString()?.toDoubleOrNull() ?: 0.0
                val decRaw = row[3]?.toString()?.toDoubleOrNull() ?: 0.0
                val magRaw = row[4]?.toString()?.toDoubleOrNull() ?: 0.0
                val plxRaw = row[5]?.toString()?.toDoubleOrNull()
                val spType = row[6]?.toString() ?: ""

                // Formatting Basic Data
                val displayName = mainIdRaw
                    .replace(Regex("^(NAME|\\*|V\\*)\\s+"), "")

                val magnitudeFormatted = String.format(Locale.US, "%.4f", magRaw) // added Locale.US
                val raFormatted = String.format(Locale.US, "%.4f", raRaw) // added Locale.US
                val decFormatted = String.format(Locale.US, "%.4f", decRaw) // added Locale.US

                val distanceFormatted = if (plxRaw != null && plxRaw > 0) {
                        String.format(Locale.US, "%.2f ly", 3261.56 / plxRaw) // added Locale.US
                } else {
                    ""
                }

                // STEP 2: Now that we have the OID, fetch ALL identifiers to find the Scientific Name (HIP/HD)
                // This ensures we get the data even if Step 2 fails or returns nothing useful, we still have Step 1's data.
                var scientificId = ""
                var commonName = "" // Used for finding name like "Sirius" if Step 1 gave "alf CMa"
                if (oid.isNotEmpty()) {
                    val queryStep2 = "SELECT id FROM ident WHERE oidref = $oid"
                    val response2 = Simbad().fetchData(queryStep2)
                    val table2 = response2?.buildStarTable()

                    if (table2 != null) {
                         for (i in 0 until table2.rowCount) {
                            val id = table2.getRow(i)[0]?.toString() ?: ""
                            if (id.startsWith("NAME ")) {
                                commonName = id.removePrefix("NAME ")
                            }
                            if (id.startsWith("HIP")) {
                                scientificId = id
                                break // HIP is preferred
                            } else if (id.startsWith("HD") && scientificId.isEmpty()) {
                                scientificId = id
                            }
                        }
                    }
                }

                // Fallback if no scientific ID found in step 2, try main_id if it looks scientific
                if (scientificId.isEmpty()) {
                     if (mainIdRaw.startsWith("HIP") || mainIdRaw.startsWith("HD")) {
                         scientificId = mainIdRaw
                     }
                }

                // Prefer commonName (e.g. "Sirius") if found, otherwise use the cleaned main_id (which might be "alf CMa")
                // If commonName is empty, we fall back to displayName from Step 1.
                val finalName = commonName.ifEmpty { displayName }

                _starState.update {
                    StarUiState(
                        name = finalName.ifEmpty { starId },
                        scientificId = scientificId,
                        magnitude = magnitudeFormatted,
                        ra = raFormatted,
                        dec = decFormatted,
                        spectralClass = spType,
                        distanceLightYears = distanceFormatted,
                        isLoading = false
                    )
                }
            } else {
                 _starState.update { it.copy(isLoading = false, name = starId) }
            }
        }
    }
}