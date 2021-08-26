package com.toyproject.booknotes.ui.detail

import androidx.lifecycle.ViewModel
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.data.BookInfoDao
import com.toyproject.booknotes.extension.runOnIoScheduler
import io.reactivex.disposables.Disposable

class DetailBookInfoViewModel(val bookInfoDao: BookInfoDao)
    : ViewModel() {

    lateinit var bookInfo:BookInfo

    fun removeBookInfo(bookInfo: BookInfo):Disposable
        = runOnIoScheduler {
        bookInfoDao.delete(bookInfo)
    }

    fun updateBookInfo(bookInfo: BookInfo):Disposable
        = runOnIoScheduler {
        bookInfoDao.update(bookInfo)
    }
}