package com.example.starccatalogue.util

import android.content.Context
import com.example.starccatalogue.network.StarOverview
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import java.io.File

interface Bookmarks {
    fun addBookmark(bookmark: StarOverview)
    fun getBookmarks(): List<StarOverview>
    fun removeBookmark(id: Int)
    fun clearBookmarks()

    fun isBookmark(oid: Int): Boolean
}
class BookmarkManager(private val context: Context, private val moshi: Moshi, val logger: Logger): Bookmarks {
    private val file: File = File(context.filesDir, "bookmarks.json")

    @OptIn(ExperimentalStdlibApi::class)
    private val adapter = moshi.adapter<MutableList<StarOverview>>()
    private val bookmarks = loadBookmarks()

    private fun loadBookmarks(): MutableList<StarOverview> {
        return if (file.exists()) {
            try {
                adapter.fromJson(file.readText()) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
    }

    override fun addBookmark(bookmark: StarOverview) {
        bookmarks.add(bookmark)
        val json = adapter.toJson(bookmarks)
        logger.i("BookmarkManager", json)
        file.writeText(json)
    }

    override fun getBookmarks(): List<StarOverview> {
        logger.d("BookmarkManager", bookmarks.toString())
        return bookmarks
    }

    override fun removeBookmark(id: Int) {
        bookmarks.removeIf { it.oid == id }
        val json = adapter.toJson(bookmarks)
        file.writeText(json)
    }

    override fun clearBookmarks() {
        file.writeText("")
        bookmarks.clear()
    }

    override fun isBookmark(oid: Int) = bookmarks.find { it.oid == oid } != null
}