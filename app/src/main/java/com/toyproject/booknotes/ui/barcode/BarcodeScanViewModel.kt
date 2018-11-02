package com.toyproject.booknotes.ui.barcode

import android.arch.lifecycle.ViewModel
import com.toyproject.booknotes.api.SearchBookApi
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.data.BookInfoDao
import com.toyproject.booknotes.extension.runOnIoScheduler
import com.toyproject.booknotes.util.SupportOptional
import com.toyproject.booknotes.util.emptyOptional
import com.toyproject.booknotes.util.optionalOf
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class BarcodeScanViewModel(val api: SearchBookApi,
                           val bookInfoDao: BookInfoDao): ViewModel() {
    val searchResult: BehaviorSubject<SupportOptional<List<BookInfo>>>
            = BehaviorSubject.createDefault(emptyOptional())

    val message:BehaviorSubject<SupportOptional<String>>
            = BehaviorSubject.create()

    val isLoading:BehaviorSubject<Boolean>
            = BehaviorSubject.createDefault(false)

    fun searchBook(query:String):Disposable
            = api.searchBook(query, 1)
            .flatMap{
                if(0 == it.meta.pageableCount){
                    Observable.error(IllegalStateException("No search result"))
                } else {
                    Observable.just(it.documents)
                }
            }
            .doOnSubscribe {
                searchResult.onNext(emptyOptional())
                message.onNext(emptyOptional())
                isLoading.onNext(true)
            }
            .doOnTerminate { isLoading.onNext(false)  }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ documents ->
                searchResult.onNext(optionalOf(documents))
            }){
                message.onNext(optionalOf(it.message ?: "Unexpected error"))
            }

    fun addToBookBasic(bookInfo:BookInfo):Disposable
            = runOnIoScheduler {
        bookInfoDao.add(bookInfo)
    }
}