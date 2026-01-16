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

interface StarDataSource {
    fun ListStars(limit : Int) : List<StarOverview>
    fun GetStarDetails(oid : Int) : StarDetails?
}

class SimbadSQLSource(val simbad: Simbad) : StarDataSource{
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun ListStars(limit: Int) : List<StarOverview>{
        val query = """
        SELECT TOP $limit oid, id, description from basic
        JOIN ident on oid = oidref and id LIKE '%NAME%'
        JOIN allfluxes using(oidref)
        JOIN otypedef using(otype)
        order by V
        """.trimIndent()

        val stars = mutableListOf<StarOverview>()
        val table = simbad.fetchData(query)?.buildStarTable() ?: return stars

        val rowSequence = table.rowSequence
        // Iterate through all rows
        while (rowSequence.next()) {
            val row = rowSequence.row
            stars.add(StarOverview(
                row[0].toString().toInt(),
                row[1].toString().substring(5),
                row[2].toString(),
            ))
        }
        // Close the sequence to free resources
        rowSequence.close()
        return stars
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun GetStarDetails(oid: Int): StarDetails? {
        val query = """
        SELECT oid, id, ra, dec, v, label, description from basic
        JOIN ident on oid = oidref
        JOIN allfluxes using(oidref)
        JOIN otypedef using(otype)
        where oid = $oid and id like 'NAME%'
        """.trimIndent()

        val table = simbad.fetchData(query)?.buildStarTable() ?: return null
        val row = table.getRow(0)
        return StarDetails(
            row[0].toString().toInt(),
            row[1].toString().substring(5),
            row[2].toString().toFloat(),
            row[3].toString().toFloat(),
            row[4].toString().toFloat(),
            row[5].toString(),
            row[6].toString(),
        )
    }
}