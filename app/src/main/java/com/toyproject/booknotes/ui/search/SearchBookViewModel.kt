package com.toyproject.booknotes.ui.search

import android.arch.lifecycle.ViewModel
import android.util.Log
import android.widget.Toast
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

class SearchBookViewModel(
        val api:SearchBookApi,
        val bookInfoDao:BookInfoDao)
    :ViewModel() {

    val searchResult:BehaviorSubject<SupportOptional<List<BookInfo>>>
        = BehaviorSubject.createDefault(emptyOptional())

    val lastSearchKeyword:BehaviorSubject<SupportOptional<String>>
        = BehaviorSubject.createDefault(emptyOptional())

    val message:BehaviorSubject<SupportOptional<String>>
        = BehaviorSubject.create()

    val isLoading:BehaviorSubject<Boolean>
        = BehaviorSubject.createDefault(false)

    val isListEnd:BehaviorSubject<Boolean>
        = BehaviorSubject.createDefault(true)

    val pageNum:BehaviorSubject<Int>
        = BehaviorSubject.createDefault(1)

    var allBookList:MutableList<BookInfo> = mutableListOf()

    fun searchBook(query:String, curPage:Int):Disposable
        = api.searchBook(query, curPage)
            .doOnNext {
                lastSearchKeyword.onNext(optionalOf(query))
                isListEnd.onNext(it.meta.isEnd)
            }
            .flatMap{
                if(0 == it.meta.pageableCount){
                    Observable.error(IllegalStateException("No search result"))
                } else {
                    Observable.just(it.documents)
                }
            }
            .doOnSubscribe {
                message.onNext(emptyOptional())
                isLoading.onNext(true)
            }
            .doOnTerminate { isLoading.onNext(false)  }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ documents ->
                allBookList.addAll(documents)
                //searchResult.onNext(optionOf(documents))
                searchResult.onNext(optionalOf(allBookList))
                pageNum.onNext(curPage)
            }){
                message.onNext(optionalOf(it.message ?: "Unexpected error"))
                searchResult.onNext(emptyOptional())
            }

    fun addToBookBasic(bookInfo:BookInfo):Disposable
            = runOnIoScheduler {
        bookInfoDao.add(bookInfo)
    }

    fun addBookItems(query:String, curPage:Int):Disposable
        = runOnIoScheduler{
        searchBook(query, curPage)
    }

    companion object {
        val TAG = "SearchBookViewModel"
    }
}