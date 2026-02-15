package com.example.starccatalogue.di

import com.example.starccatalogue.network.Simbad
import com.example.starccatalogue.network.SimbadSQLSource
import com.example.starccatalogue.network.StarDataSource
import com.example.starccatalogue.ui.bookmark.BookmarkViewModel
import com.example.starccatalogue.ui.home.HomeViewModel
import com.example.starccatalogue.ui.list.ListVM
import com.example.starccatalogue.ui.stars.StarsViewModel
import com.example.starccatalogue.util.AndroidLogger
import com.example.starccatalogue.util.BookmarkManager
import com.example.starccatalogue.util.Bookmarks
import com.example.starccatalogue.util.Logger
import com.squareup.moshi.Moshi
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val AppModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ListVM)
    viewModel { (starId: String) -> StarsViewModel(starId, get()) }
    viewModelOf(::BookmarkViewModel)

    single { Moshi.Builder().build() }

    singleOf(::AndroidLogger) bind Logger::class
    singleOf(::SimbadSQLSource) bind StarDataSource::class
    singleOf(::BookmarkManager) bind Bookmarks::class
    single { Simbad(get()) }
}
