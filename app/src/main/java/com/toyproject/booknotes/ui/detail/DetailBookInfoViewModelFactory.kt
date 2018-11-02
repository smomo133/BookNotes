package com.toyproject.booknotes.ui.detail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.toyproject.booknotes.data.BookInfoDao

class DetailBookInfoViewModelFactory(val bookInfoDao:BookInfoDao)
    :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailBookInfoViewModel(bookInfoDao) as T
    }
}