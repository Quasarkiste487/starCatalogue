package com.example.starccatalogue

import com.example.starccatalogue.network.QueryScript
import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.printAll
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    var base = "https://simbad.cds.unistra.fr/simbad/sim-script"

    @Test
    fun fetchStardata() {
        val queryScript = QueryScript(10, listOf("main_id", "coordinates", "flux(V)"), "Vmag < 6")
        val simbadResponse = Simbad().fetchData(queryScript)
        println(simbadResponse.getHeaderMetadata())

        val starTable = simbadResponse.buildStarTable()
        starTable.printAll()
    }
}
