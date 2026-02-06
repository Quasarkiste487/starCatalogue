package com.example.starccatalogue

import com.example.starccatalogue.network.Filter
import com.example.starccatalogue.network.QueryScript
import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.SimbadResponse
import com.example.starccatalogue.network.SimbadSQLSource
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
        val logger = com.example.starccatalogue.util.StdoutLogger()

        val script = QueryScript(10, listOf("main_id", "ra", "dec", "flux(V)"), "Vmag < 6")
        var raw: SimbadResponse?
        println("fetched data in: " + measureTime {
            raw = Simbad(logger).fetchData(script)
        })

        if (raw == null) {
            return
        }

        println(raw.getHeaderMetadata())
        var table: StarTable
        println("built table in: " + measureTime {
            table = raw.buildStarTable()
        })

        table.printAll()
    }

    @Test
    fun fetchStardataSQL() {
        val query = """
        SELECT TOP 100 oidref, id from ident
        JOIN allfluxes using(oidref)
        order by V
        """.trimIndent()

        val logger = com.example.starccatalogue.util.StdoutLogger()

        var raw: SimbadResponse?
        println("fetched data in: " + measureTime {
            raw = Simbad(logger).fetchData(query)
        })
        if (raw == null) {
            return
        }
        println(raw.getHeaderMetadata())
        var table: StarTable
        println("built table in: " + measureTime {
            table = raw.buildStarTable()
        })

        table.printAll()
    }

    @Test
    fun fetchDataRepo() {
        val logger = com.example.starccatalogue.util.StdoutLogger()

        val repo = SimbadSQLSource(Simbad(logger))
        val result =
            repo.listStarsRequest().limit(10).filter("ident", Filter.like("id", "NAME %Sirius%"))
                .fetch()
        result.forEach { println(it) }

        val first = repo.getStarDetails(result[0].oid)
        println("Sirius: ")
        println(first)
    }
}
