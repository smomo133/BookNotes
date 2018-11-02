package com.toyproject.booknotes.di.ui

import com.toyproject.booknotes.api.SearchBookApi
import com.toyproject.booknotes.data.BookInfoDao
import com.toyproject.booknotes.ui.barcode.BarcodeScanVIewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ZxingScannerModule {
    @Provides
    fun provideViewModelFactory(api:SearchBookApi, info: BookInfoDao): BarcodeScanVIewModelFactory
            = BarcodeScanVIewModelFactory(api, info)
}