package com.toyproject.booknotes.di.ui

import com.toyproject.booknotes.data.BookInfoDao
import com.toyproject.booknotes.ui.detail.DetailBookInfoViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class DetailBookInfoModule {

    @Provides
    fun provideViewModelFactory(info:BookInfoDao):DetailBookInfoViewModelFactory
        = DetailBookInfoViewModelFactory(info)
}