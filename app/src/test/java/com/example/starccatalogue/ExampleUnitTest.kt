package com.example.starccatalogue

import com.example.starccatalogue.network.Script
import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.printAll
import org.junit.Test
import uk.ac.starlink.table.StarTable
import uk.ac.starlink.table.StarTableFactory
import uk.ac.starlink.votable.VOTableBuilder
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.time.measureTime


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    var base = "https://simbad.cds.unistra.fr/simbad/sim-script"

    @Test
    fun fetchStardata() {
        val script = Script(10, listOf("main_id", "coordinates", "flux(V)"), "Vmag < 6")
        val raw = Simbad().fetch(script)
        println(raw.header())

        val table = raw.build()
        table.printAll()
    }
}
