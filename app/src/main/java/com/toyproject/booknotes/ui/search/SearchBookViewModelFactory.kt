package com.toyproject.booknotes.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyproject.booknotes.api.SearchBookApi
import com.toyproject.booknotes.data.BookInfoDao

class SearchBookViewModelFactory(val api:SearchBookApi,
                                 val bookInfoDao:BookInfoDao)
    :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchBookViewModel(api, bookInfoDao) as T
    }
}