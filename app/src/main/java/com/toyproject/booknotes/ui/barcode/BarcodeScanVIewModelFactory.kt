package com.toyproject.booknotes.ui.barcode

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.toyproject.booknotes.api.SearchBookApi
import com.toyproject.booknotes.data.BookInfoDao

class BarcodeScanVIewModelFactory(val api: SearchBookApi,
                                  val bookInfoDao: BookInfoDao):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BarcodeScanViewModel(api, bookInfoDao) as T
    }
}