package com.example.starccatalogue

import android.app.Application
import org.koin.core.context.startKoin
import com.example.starccatalogue.di.AppModule
import org.koin.android.ext.koin.androidContext

class StarCatalogue : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StarCatalogue)
            modules(AppModule)
        }
    }
}
