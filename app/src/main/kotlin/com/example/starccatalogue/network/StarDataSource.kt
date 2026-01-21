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
     * Creates a new query builder for listing stars with basic information.
     *
     * The returned [QueryBuilder] allows callers to construct a query using a fluent
     * API, optionally adding filters, ordering, and limiting the result set before
     * fetching the data.
     *
     * @return a [QueryBuilder] instance that can be configured and executed to retrieve
     *   a list of [StarOverview] entries.
     */
    fun listStarsRequest() : QueryBuilder

    /**
     * Retrieves detailed information about a single star identified by [oid].
     *
     * @param oid unique object identifier of the star in the underlying catalogue.
     * @return a [StarDetails] instance for the requested star, or `null` if no
     *   star with the given [oid] exists in the data source.
     */
    fun getStarDetails(oid : Int) : StarDetails?
}

/**
 * Represents a filter condition for database queries.
 *
 * A filter specifies a field name, a comparison operator, and a value to match against.
 * Filters are used to narrow down query results based on specific criteria.
 *
 * @property field the name of the database field to filter on.
 * @property operator the comparison operator (e.g., "=", "LIKE", ">", "<").
 * @property value the value to compare against (should be properly escaped/quoted for SQL).
 */
data class Filter(val field : String, val operator : String = "=", val value : String){
    companion object{
        /**
         * Creates a filter that matches field values containing the given substring.
         *
         * @param field the database field name to search in.
         * @param value the substring to search for.
         * @return a Filter using the LIKE operator with wildcards on both sides.
         */
        fun contains(field: String, value : String) = like(field, "%$value%")

        /**
         * Creates a filter using the LIKE operator with a custom pattern.
         *
         * @param field the database field name to match against.
         * @param value the LIKE pattern (may include % and _ wildcards).
         * @return a Filter using the LIKE operator.
         */
        fun like(field: String, value : String) = Filter(field, "LIKE", "'$value'")
    }

    /**
     * Builds the SQL WHERE clause fragment for this filter.
     *
     * @param table the table name to prefix the field with.
     * @return a SQL fragment in the form "table.field operator value".
     */
    fun build(table : String) = "${table}.$field $operator $value"
}

/**
 * Represents the ordering specification for query results.
 *
 * An ordering defines which field to sort by and in which direction (ascending or descending).
 *
 * @property table the table name containing the field to sort by.
 * @property field the field name to use for sorting.
 * @property direction the sort direction, either "ASC" (ascending) or "DESC" (descending). Defaults to "ASC".
 */
data class Ordering(val table : String, val field : String, val direction: String = "ASC"){
    /**
     * Builds the SQL ORDER BY clause for this ordering.
     *
     * @return a SQL fragment in the form "ORDER BY field direction".
     */
    fun build() = "ORDER BY ${this.field} ${this.direction}"
}

/**
 * A fluent API builder for constructing and executing star catalogue queries.
 *
 * This interface provides a chainable method pattern for building complex queries
 * with filters, ordering, and result limiting. After configuring the query through
 * the builder methods, call [fetch] to execute the query and retrieve results.
 *
 * Example usage:
 * ```
 * val stars = queryBuilder
 *     .filter("basic", Filter.contains("name", "Sirius"))
 *     .order(Ordering("basic", "mag", "ASC"))
 *     .limit(10)
 *     .fetch()
 * ```
 */
interface QueryBuilder{
    /**
     * Adds a filter condition to the query for a specific table.
     *
     * Multiple filters can be added to the same table, and they will be combined
     * using logical AND. Filters on different tables are also combined with AND.
     *
     * @param table the name of the database table to apply the filter to.
     * @param filter the filter condition to apply.
     * @return this QueryBuilder instance for method chaining.
     */
    fun filter(table: String, filter: Filter) : QueryBuilder

    /**
     * Sets the ordering for the query results.
     *
     * Only one ordering can be active at a time. If this method is called multiple
     * times, the last ordering specified will be used.
     *
     * @param ordering the ordering specification defining which field to sort by and the direction.
     * @return this QueryBuilder instance for method chaining.
     */
    fun order(ordering: Ordering) : QueryBuilder

    /**
     * Limits the maximum number of results returned by the query.
     *
     * The actual number of results may be less than the limit if fewer matching
     * records exist in the database.
     *
     * @param limit the maximum number of [StarOverview] entries to return. Must be positive.
     * @return this QueryBuilder instance for method chaining.
     */
    fun limit(limit : Int) : QueryBuilder

    /**
     * Executes the query and retrieves the results.
     *
     * This method builds the final query using all configured filters, ordering,
     * and limits, then executes it against the data source.
     *
     * @return a list of [StarOverview] entries matching the query criteria.
     *   Returns an empty list if no matches are found or if an error occurs.
     */
    fun fetch() : List<StarOverview>
}

private class ADQLQueryBuilder(val fetchFunction: (String) -> List<StarOverview>) : QueryBuilder{
    private var filterMap : MutableMap<String, MutableList<Filter>> = mutableMapOf()
    private var ordering: Ordering? = null
    private var limit : Int? = null

    override fun fetch(): List<StarOverview> {
        return fetchFunction(this.build())
    }

    override fun filter(table: String, filter: Filter) : ADQLQueryBuilder{
        val filters = filterMap[table]
        if (filters != null){
            filters.add(filter)
        }else{
            filterMap[table] = mutableListOf(filter)
        }
        return this
    }

    override fun order(ordering: Ordering) : ADQLQueryBuilder{
        this.ordering = ordering
        return this
    }

    override fun limit(limit : Int) : ADQLQueryBuilder{
        this.limit = limit
        return this
    }

    fun build() : String{
        val builder = StringBuilder()
        builder.appendLine("""
            SELECT ${if (this.limit != null) "TOP $limit" else ""} 
                basic.oid,
                ident.id,
                otypedef.description
            FROM ident
            JOIN basic on basic.oid = ident.oidref""".trimIndent())
        filterMap["basic"]?.forEach { builder.appendLine("AND ${it.build("basic")}") }
        filterMap["ident"]?.forEach { builder.appendLine("AND ${it.build("ident")}") }
        builder.appendLine("LEFT JOIN otypedef ON otypedef.otype = basic.otype")
        filterMap["otypedef"]?.forEach { builder.appendLine("AND ${it.build("otypedef")}") }

        filterMap.forEach { it ->
            if (it.key in arrayOf("basic","ident","otypedef")){
                return@forEach
            }
            val table = it.key
            builder.appendLine("JOIN $table ON ${table}.oidref = ident.oidref")
            it.value.forEach { builder.appendLine("AND ${it.build(table)}") }
        }
        ordering?.let {
            if (filterMap[it.table] == null){
                builder.appendLine("LEFT JOIN ${it.table} ON ${it.table}.oidref = ident.oidref")
            }
            builder.appendLine(it.build())
        }
        return builder.toString()
    }
}

class SimbadSQLSource(val simbad: Simbad) : StarDataSource{
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun listStarsRequest() : QueryBuilder {
        return ADQLQueryBuilder(fun (query : String) : List<StarOverview>{
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
                return mutableListOf()
            }

            // Close the sequence to free resources
            rowSequence.close()
            return stars
        })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun getStarDetails(oid: Int): StarDetails? {
        val query = """
        SELECT oid, id, ra, dec, v, label, description from basic
        JOIN ident on oid = oidref
        LEFT JOIN allfluxes using(oidref)
        LEFT JOIN otypedef using(otype)
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
