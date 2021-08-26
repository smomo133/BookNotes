package com.toyproject.booknotes.di.ui

import com.toyproject.booknotes.data.BookInfoDao
import com.toyproject.booknotes.ui.books.BookcaseActivity
import com.toyproject.booknotes.ui.books.BookcaseAdapter
import com.toyproject.booknotes.ui.books.BookcaseViewModelFactory

import dagger.Module
import dagger.Provides

@Module
class BookcaseModule {

    @Provides
    fun provideAdapter(activity: BookcaseActivity):BookcaseAdapter
            = BookcaseAdapter().apply { setItemClickListener(activity) }

    @Provides
    fun provideViewModelFactory(bookInfoDao:BookInfoDao):BookcaseViewModelFactory
        = BookcaseViewModelFactory(bookInfoDao)
}
