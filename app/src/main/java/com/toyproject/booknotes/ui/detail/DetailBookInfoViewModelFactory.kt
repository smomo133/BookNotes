package com.toyproject.booknotes.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyproject.booknotes.data.BookInfoDao

class DetailBookInfoViewModelFactory(val bookInfoDao:BookInfoDao)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailBookInfoViewModel(bookInfoDao) as T
    }
}