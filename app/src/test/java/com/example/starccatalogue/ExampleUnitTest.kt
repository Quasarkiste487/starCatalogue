package com.example.starccatalogue

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
        val script = Script(10, listOf("main_id", "coordinates", "flux(V)"), "Vmag < 6").build()

        val url = base + "?script=" + URLEncoder.encode(script, StandardCharsets.UTF_8)
        val connection = URL(url).openConnection()
        var stream : InputStream
        var meta : Map<String, String>
        var table : StarTable

        println("request time: " + measureTime {
            stream = connection.getInputStream()
        })

        println("header parsing time " + measureTime {
            meta = parseStream(stream)
        })

        //println("body")
        //stream.bufferedReader().forEachLine { println(it) }

        println("table building time: " + measureTime {
            table = StarTableFactory().makeStarTable(stream, VOTableBuilder())
        })

        println(meta)
        writeTable(table)
    }
}

fun parseStream(inputStream: InputStream) : Map<String, String>{
    val metadata = HashMap<String, String>()
    var currentSegment : String? = null

    val buffer = StringBuilder()

    for (line in inputStream.readLinesStrictUntil("<\\?xml.*\\?>".toRegex())){
        if (line.startsWith("::")){ // simplify check
            // Process previous buffer before starting new segment
            if (currentSegment != null){
                // println("Finished $currentSegment with ${buffer.length} chars")
                metadata[currentSegment] = buffer.toString()
            }
            currentSegment = line.substringAfter("::").substringBefore(":")
            buffer.clear()
        } else {
            buffer.appendLine(line)
        }
    }
    return metadata
}

fun InputStream.readLinesStrictUntil(delim: Regex): List<String> {
    val lines = mutableListOf<String>()
    do {
        val sb = StringBuilder()
        var byte: Int
        while (true) {
            byte = this.read()
            if (byte == -1 || byte == '\n'.code) break
            if (byte != '\r'.code) sb.append(byte.toChar())
        }
        if (byte == -1 && sb.isEmpty()) break
        lines.add(sb.toString())
    } while (!delim.matches(sb.toString()))
    return lines
}

class Script(val limit : Int, val fields: List<String>, val criteria: String){
    public fun build() : String =
        """
        set limit $limit
        votable ${fields.joinToString(separator = ",", prefix = "{", postfix = "}")}
        votable open
        query sample $criteria
    """.trimIndent()
}

@Throws(IOException::class)
fun writeTable(table: StarTable) {
    val nCol = table.columnCount
    val rseq = table.rowSequence
    while (rseq.next()) {
        val row = rseq.row
        for (icol in 0..<nCol) {
            print(row[icol].toString() + "\t")
        }
        println()
    }
    rseq.close()
}