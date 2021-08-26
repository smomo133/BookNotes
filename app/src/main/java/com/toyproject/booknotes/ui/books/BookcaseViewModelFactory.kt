package com.toyproject.booknotes.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyproject.booknotes.data.BookInfoDao

class BookcaseViewModelFactory(val bookInfoDao: BookInfoDao): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return BookcaseViewModel(bookInfoDao) as T
    }
}