package com.example.starccatalogue

import com.example.starccatalogue.network.QueryScript
import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.SimbadResponse
import com.example.starccatalogue.network.printAll
import org.junit.Test
import uk.ac.starlink.table.StarTable
import kotlin.time.measureTime


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    var base = "https://simbad.cds.unistra.fr/simbad/sim-script"

    @Test
    fun fetchStardataScript() {
        val script = QueryScript(10, listOf("main_id", "ra", "dec", "flux(V)"), "Vmag < 6")
        var raw : SimbadResponse
        println("fetched data in: " + measureTime {
            raw = Simbad().fetchData(script)
        })
        println(raw.getHeaderMetadata())
        var table : StarTable
        println("built table in: " + measureTime {
            table = raw.buildStarTable()
        })

        table.printAll()
    }

    @Test
    fun fetchStardataSQL(){
        val query = """
            SELECT TOP 100 id, V as mag, ra, dec  from ident
JOIN allfluxes USING(oidref)
JOIN basic on oid = oidref
WHERE id LIKE '%NAME%'
ORDER BY V
        """.trimIndent()

        var raw : SimbadResponse
        println("fetched data in: " + measureTime {
            raw = Simbad().fetchData(query)
        })
        println(raw.getHeaderMetadata())
        var table : StarTable
        println("built table in: " + measureTime {
            table = raw.buildStarTable()
        })

        table.printAll()
    }
}
