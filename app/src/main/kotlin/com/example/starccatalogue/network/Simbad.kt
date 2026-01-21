package com.example.starccatalogue.network

import android.net.http.NetworkException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import com.example.starccatalogue.util.Logger
import okhttp3.internal.connection.Exchange
import uk.ac.starlink.table.StarTable
import uk.ac.starlink.table.StarTableFactory
import uk.ac.starlink.votable.VOTableBuilder
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Client for fetching astronomical data from the SIMBAD database.
 * Supports configurable mirror URLs for redundancy.
 */
class Simbad (val logger : Logger, private val mirrorUrl : String = DEFAULT_MIRROR){
    companion object{
        // Default SIMBAD mirror URL (France)
        const val DEFAULT_MIRROR = "https://simbad.cds.unistra.fr/simbad"
    }

    /**
     * Fetches data from SIMBAD by executing a script query.
     * @param queryScript The query script to execute
     * @return A SimbadResponse containing the response stream
     */
    fun fetchData(url: URL) : SimbadResponse?{
        logger.i("Simbad", "Fetching data from URL: $url")
        try{
            val responseStream = url.openConnection().getInputStream()
            return SimbadResponse(logger, responseStream = responseStream)
        }catch(e : Exception) {
            print("Exception occured while fetching data: $e")
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun fetchData(queryScript: QueryScript): SimbadResponse? {
        // Build the complete URL with encoded script parameters
        val requestUrl = "$mirrorUrl/sim-script?script=" + URLEncoder.encode(queryScript.build(), StandardCharsets.UTF_8)
        // Establish connection and get input stream from SIMBAD server

        return fetchData(URL(requestUrl))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun fetchData(sqlQuery: String): SimbadResponse? {
        val requestUrl = "$mirrorUrl/sim-tap/sync?request=doQuery&lang=adql&format=votable&query=" + URLEncoder.encode(sqlQuery,
            StandardCharsets.UTF_8)
        // println("URL: $requestUrl")

        return fetchData(URL(requestUrl))
    }
}

/**
 * Represents the raw response from a SIMBAD query.
 * Parses header information from the response and provides access to the underlying table data.
 */
class SimbadResponse(val logger : Logger, val responseStream : InputStream){
    // Stores metadata sections from the response (e.g., warnings, info)
    private var headerMetadata = HashMap<String, String>()

    init {
        // Current section name being parsed from response header
        var currentSectionName : String? = null
        // Buffer to accumulate lines for the current section
        val sectionContentBuffer = StringBuilder()

        // Parse response lines until the XML declaration is found
        for (line in responseStream.readLinesUntilXmlStart("<\\?xml.*\\?>".toRegex())){
            if (line.startsWith("::")){
                // New section detected; save the previous section if it exists
                if (currentSectionName != null){
                    logger.i("Simbad","Read $currentSectionName with ${sectionContentBuffer.length} chars")
                    headerMetadata[currentSectionName] = sectionContentBuffer.toString()
                }
                // Extract section name (between "::" and ":")
                currentSectionName = line.substringAfter("::").substringBefore(":")
                sectionContentBuffer.clear()
            } else {
                // Accumulate content for the current section
                sectionContentBuffer.appendLine(line)
            }
        }
    }

    /**
     * Returns the parsed header metadata from the response.
     */
    fun getHeaderMetadata() : Map<String, String> = headerMetadata

    /**
     * Builds a StarTable from the VOTable XML data in the response.
     * @throws Error if an error message is present in the response headers
     * @return A StarTable object containing the parsed astronomical data
     */
    fun buildStarTable(): StarTable {
        // Check for errors reported by SIMBAD
        val errorMessage = error()
        if (errorMessage != null){
            throw Error(errorMessage)
        }
        // Parse the VOTable XML format into a StarTable
        return StarTableFactory().makeStarTable(responseStream, VOTableBuilder())
    }

    fun error(): String? = headerMetadata["error"]
}

/**
 * Reads lines from an InputStream until a line matches the given regex delimiter (XML start).
 * Handles line endings (CR/LF) correctly across different platforms.
 * @param xmlDelimiterPattern The regex pattern to match for stopping (typically XML declaration)
 * @return A list of lines read up to and including the delimiter
 */
internal fun InputStream.readLinesUntilXmlStart(xmlDelimiterPattern: Regex): List<String> {
    val linesList = mutableListOf<String>()
    do {
        val currentLineBuilder = StringBuilder()
        var currentByte: Int
        // Read bytes until end of line or end of stream
        while (true) {
            currentByte = this.read()
            // Stop on newline or EOF
            if (currentByte == -1 || currentByte == '\n'.code) break
            // Skip carriage returns for cross-platform compatibility
            if (currentByte != '\r'.code) currentLineBuilder.append(currentByte.toChar())
        }
        // Break if we reached EOF with an empty line
        if (currentByte == -1 && currentLineBuilder.isEmpty()) break
        linesList.add(currentLineBuilder.toString())
    } while (!xmlDelimiterPattern.matches(currentLineBuilder.toString()))
    return linesList
}

/**
 * Prints all rows and columns of a StarTable in a tab-separated format.
 * @throws IOException if an error occurs while reading the table
 */
@Throws(IOException::class)
fun StarTable.printAll() {
    for (i in 0..<columnCount){
        print(getColumnInfo(i).name + "\t")
    }
    println()

    val columnCount = this.columnCount
    val rowSequence = this.rowSequence
    // Iterate through all rows
    while (rowSequence.next()) {
        val currentRow = rowSequence.row
        // Print each column value separated by tabs
        for (columnIndex in 0..<columnCount) {
            print(currentRow[columnIndex].toString() + "\t")
        }
        currentRow[0]
        // Move to next row
        println()
    }
    // Close the sequence to free resources
    rowSequence.close()
}

/**
 * Represents a SIMBAD query script with configurable parameters.
 * Builds a script string compatible with SIMBAD's scripting language.
 *
 * @param limit Maximum number of results to return
 * @param fields List of data fields to include in the VOTable response
 * @param criteria Query criteria for filtering astronomical objects
 */
class QueryScript(val limit : Int, val fields: List<String>, val criteria: String){
    /**
     * Builds the complete SIMBAD query script with proper formatting.
     * @return A formatted script string for submission to SIMBAD
     */
    fun build() : String =
        """
        set limit $limit
        votable ${fields.joinToString(separator = ",", prefix = "{", postfix = "}")}
        votable open
        query sample $criteria
    """.trimIndent()
}
