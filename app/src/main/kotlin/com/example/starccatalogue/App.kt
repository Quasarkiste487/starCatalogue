package com.example.starccatalogue

import android.app.Application
import org.koin.core.context.startKoin
import com.example.starccatalogue.di.AppModule

class StarCatalogue : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(AppModule)
        }
    }
}
