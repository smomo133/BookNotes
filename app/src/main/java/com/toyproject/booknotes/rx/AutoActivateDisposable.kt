package com.toyproject.booknotes.rx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

class AutoActivateDisposable(
        private val lifecycleOwner: LifecycleOwner,
        private val func:()->Disposable
    )
    : LifecycleObserver {

    private var disposable:Disposable? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activite(){
        disposable = func.invoke()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun deactive(){
        disposable?.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf(){
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}