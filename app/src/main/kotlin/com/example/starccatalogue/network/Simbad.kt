package com.example.starccatalogue.network

import android.os.Build
import androidx.annotation.RequiresApi
import uk.ac.starlink.table.StarTable
import uk.ac.starlink.table.StarTableFactory
import uk.ac.starlink.votable.VOTableBuilder
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Simbad (private val mirror : String = FR){
    companion object{
        const val FR = "https://simbad.cds.unistra.fr/simbad"
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun fetch(script: Script): RawTable {
        val url = mirror + "/sim-script?script=" + URLEncoder.encode(script.build(), StandardCharsets.UTF_8)
        val stream = URL(url).openConnection().getInputStream()
        return RawTable(iStream = stream)
    }
}

class RawTable(val iStream : InputStream){
    private var header = HashMap<String, String>()

    init {
        var currentSegment : String? = null
        val buffer = StringBuilder()

        for (line in iStream.readLinesStrictUntil("<\\?xml.*\\?>".toRegex())){
            if (line.startsWith("::")){
                if (currentSegment != null){
                    println("Read $currentSegment with ${buffer.length} chars")
                    header[currentSegment] = buffer.toString()
                }
                currentSegment = line.substringAfter("::").substringBefore(":")
                buffer.clear()
            } else {
                buffer.appendLine(line)
            }
        }
    }

    fun header() : Map<String, String> = header

    fun build(): StarTable {
        val error = header["error"]
        if (error != null){
            throw Error(error)
        }
        return StarTableFactory().makeStarTable(iStream, VOTableBuilder())
    }
}

internal fun InputStream.readLinesStrictUntil(delim: Regex): List<String> {
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

@Throws(IOException::class)
fun StarTable.printAll() {
    val nCol = this.columnCount
    val rseq = this.rowSequence
    while (rseq.next()) {
        val row = rseq.row
        for (icol in 0..<nCol) {
            print(row[icol].toString() + "\t")
        }
        println()
    }
    rseq.close()
}

class Script(val limit : Int, val fields: List<String>, val criteria: String){
    fun build() : String =
        """
        set limit $limit
        votable ${fields.joinToString(separator = ",", prefix = "{", postfix = "}")}
        votable open
        query sample $criteria
    """.trimIndent()
}