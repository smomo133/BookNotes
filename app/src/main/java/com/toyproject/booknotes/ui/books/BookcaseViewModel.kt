package com.toyproject.booknotes.ui.books

import androidx.lifecycle.ViewModel
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.data.BookInfoDao
import com.toyproject.booknotes.extension.runOnIoScheduler
import com.toyproject.booknotes.util.SupportOptional
import com.toyproject.booknotes.util.emptyOptional
import com.toyproject.booknotes.util.optionalOf
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

@HiltViewModel
class BookcaseViewModel @Inject constructor(val bookInfoDao: BookInfoDao): ViewModel() {

    val searchResult:BehaviorSubject<SupportOptional<List<BookInfo>>>
        = BehaviorSubject.createDefault(emptyOptional())

    val lastSearchKeyword:BehaviorSubject<SupportOptional<String>>
        = BehaviorSubject.createDefault(emptyOptional())

    val message:BehaviorSubject<SupportOptional<String>>
            = BehaviorSubject.create()

    var isEditOpen:Boolean = false

    val bookcaseList: Flowable<SupportOptional<List<BookInfo>>>
        get() = bookInfoDao.getAllBookList()
                .map{ optionalOf(it)}
                .doOnNext{ optional->
                    if(optional.value.isEmpty()){
                        message.onNext(optionalOf("No lists"))
                    } else {
                        message.onNext(emptyOptional())
                    }
                }
                .doOnError{
                    //
                    message.onNext(optionalOf(it.message ?: "Unexpected error"))
                }
                .onErrorReturn { emptyOptional() }

    fun searchBook(query:String):Disposable
        = bookInfoDao.getSearchBookList(query)
            .doOnNext { lastSearchKeyword.onNext(optionalOf(query)) }
            .doOnSubscribe {
                //searchResult.onNext(emptyOptional())
                message.onNext(emptyOptional())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                searchResult.onNext(optionalOf(it))
            }){
                message.onNext(optionalOf(it.message ?: "Unexpected error"))
                searchResult.onNext(emptyOptional())
            }

    fun removeBookInfoList(list:List<BookInfo>):Disposable
     = runOnIoScheduler {
        bookInfoDao.deleteBookinfoList(list)
    }
}