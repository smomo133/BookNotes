package com.toyproject.booknotes.di.ui

import com.toyproject.booknotes.api.SearchBookApi
import com.toyproject.booknotes.data.BookInfoDao
import com.toyproject.booknotes.ui.search.SearchBookActivity
import com.toyproject.booknotes.ui.search.SearchBookAdapter
import com.toyproject.booknotes.ui.search.SearchBookViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SearchBookModule {
    @Provides
    fun provideAdapter(activity:SearchBookActivity):SearchBookAdapter
        = SearchBookAdapter().apply { setItemClickListener(activity) }

    @Provides
    fun provideViewModelFactory(api:SearchBookApi, info:BookInfoDao):SearchBookViewModelFactory
        = SearchBookViewModelFactory(api, info)
}