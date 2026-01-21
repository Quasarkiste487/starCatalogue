package com.example.starccatalogue.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.SimbadSQLSource
import com.example.starccatalogue.network.StarDataSource
import com.example.starccatalogue.ui.home.HomeViewModel
import com.example.starccatalogue.ui.list.ListVM
import com.example.starccatalogue.ui.stars.StarsViewModel
import com.example.starccatalogue.util.AndroidLogger
import com.example.starccatalogue.util.Logger
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.bind
import kotlin.math.sin

val AppModule = module{
    viewModelOf(::HomeViewModel)
    viewModelOf(::ListVM)
    viewModelOf(::StarsViewModel)

    singleOf(::AndroidLogger) bind Logger::class
    singleOf(::SimbadSQLSource) bind StarDataSource::class
    single{Simbad(get())}
}
