package com.example.starccatalogue.util
import android.util.Log

interface Logger {
    fun i(tag: String, message: String)
    fun d(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

class AndroidLogger : Logger {
    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}

class StdoutLogger : Logger {
    override fun i(tag: String, message: String) {
        println("INFO: [$tag] $message")
    }

    override fun d(tag: String, message: String) {
        println("DEBUG: [$tag] $message")
    }

    override fun w(tag: String, message: String) {
        println("WARN: [$tag] $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        println("ERROR: [$tag] $message")
        throwable?.printStackTrace()
    }
}
