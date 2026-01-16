package com.example.starccatalogue.network

import android.os.Build
import androidx.annotation.RequiresApi

data class StarOverview(val oid : Int, val name : String, val typ : String)
data class StarDetails(
    val oid : Int,
    val name : String,
    val ra : Float,
    val dec : Float,
    val mag : Float,
    val shortType : String,
    val type : String,
    )

/**
 * Abstraction for retrieving star catalogue data.
 *
 * Implementations are responsible for providing overview lists of stars as well
 * as detailed information for a single star, typically by querying a remote or
 * local data source.
 *
 * Implementations may throw implementation-specific exceptions (for example,
 * networking or database errors). Callers should handle such failures according
 * to their needs.
 */
interface StarDataSource {
    /**
     * Returns a list of at most [limit] stars with basic information.
     *
     * @param limit maximum number of stars to return. Implementations should not
     *   return more than this number of items; they may return fewer.
     * @return a list of [StarOverview] entries. If no stars are available or an
     *   empty result is produced by the data source, this method returns an
     *   empty list.
     */
    fun listStars(limit : Int, name : String) : List<StarOverview>

    /**
     * Returns detailed information about a single star identified by [oid].
     *
     * @param oid unique object identifier of the star in the underlying
     *   catalogue.
     * @return a [StarDetails] instance for the requested star, or `null` if no
     *   star with the given [oid] exists in the data source.
     */
    fun getStarDetails(oid : Int) : StarDetails?
}

class SimbadSQLSource(val simbad: Simbad) : StarDataSource{
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun listStars(limit: Int, name : String) : List<StarOverview>{
        val query = """
        SELECT TOP $limit oid, id, description from basic
        JOIN ident on oid = oidref and id LIKE '%NAME %'
        JOIN allfluxes using(oidref)
        JOIN otypedef using(otype)
        WHERE id LIKE '%$name%'
        order by V
        """.trimIndent()

        val stars = mutableListOf<StarOverview>()
        val table = simbad.fetchData(query)?.buildStarTable() ?: return stars

        val rowSequence = table.rowSequence
        try {
            // Iterate through all rows
            while (rowSequence.next()) {
                val row = rowSequence.row
                stars.add(StarOverview(
                    row[0].toString().toInt(),
                    row[1].toString().substring(5),
                    row[2].toString(),
                ))
            }
        }catch(_: Exception){
            println("Invalid table row: ${rowSequence.row}")
            return mutableListOf<StarOverview>()
        }

        // Close the sequence to free resources
        rowSequence.close()
        return stars
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun getStarDetails(oid: Int): StarDetails? {
        val query = """
        SELECT oid, id, ra, dec, v, label, description from basic
        JOIN ident on oid = oidref
        JOIN allfluxes using(oidref)
        JOIN otypedef using(otype)
        where oid = $oid and id like 'NAME%'
        """.trimIndent()

        val table = simbad.fetchData(query)?.buildStarTable() ?: return null
        val row = table.getRow(0)
        try {
            return StarDetails(
                row[0].toString().toInt(),
                row[1].toString().substring(5),
                row[2].toString().toFloat(),
                row[3].toString().toFloat(),
                row[4].toString().toFloat(),
                row[5].toString(),
                row[6].toString(),
            )
        }catch(_: Exception){
            println("Invalid table row: $row")
            return null
        }
    }
}