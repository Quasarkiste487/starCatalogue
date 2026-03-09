package com.example.starccatalogue

import android.app.Application
import com.example.starccatalogue.di.AppModule
import com.example.starccatalogue.util.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StarCatalogue : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StarCatalogue)
            modules(AppModule)
        }

        val settings: Settings by inject()
        applicationScope.launch {
            settings.initialize()
        }
    }
}
