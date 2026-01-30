package com.example.starccatalogue.util

import android.content.Context
import com.example.starccatalogue.network.StarOverview
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

interface Bookmarks {
    val bookmarksFlow: StateFlow<List<StarOverview>>
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
    private val lock = Any()
    private val bookmarks = loadBookmarks()
    private val _bookmarksFlow = MutableStateFlow<List<StarOverview>>(bookmarks)
    override val bookmarksFlow: StateFlow<List<StarOverview>> = _bookmarksFlow.asStateFlow()


    private fun loadBookmarks(): MutableList<StarOverview> {
        return synchronized(lock) {
            if (file.exists()) {
                try {
                    adapter.fromJson(file.readText()) ?: mutableListOf()
                } catch (e: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }
        }
    }

    override fun addBookmark(bookmark: StarOverview) {
        synchronized(lock) {
            bookmarks.add(bookmark)
            val json = adapter.toJson(bookmarks)
            logger.i("BookmarkManager", json)
            file.writeText(json)
            _bookmarksFlow.value = bookmarks.toList()
        }
    }

    override fun getBookmarks(): List<StarOverview> {
        return synchronized(lock) {
            logger.d("BookmarkManager", bookmarks.toString())
            bookmarks
        }
    }

    override fun removeBookmark(id: Int) {
        synchronized(lock) {
            bookmarks.removeIf { it.oid == id }
            val json = adapter.toJson(bookmarks)
            file.writeText(json)
            _bookmarksFlow.value = bookmarks.toList()
        }
    }

    override fun clearBookmarks() {
        synchronized(lock) {
            file.writeText("")
            bookmarks.clear()
            _bookmarksFlow.value = emptyList()
        }
    }

    override fun isBookmark(oid: Int): Boolean {
        return synchronized(lock) {
            bookmarks.find { it.oid == oid } != null
        }
    }
}