package com.toyproject.booknotes.rx

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

class AutoActivateDisposable(
        private val lifecycleOwner: LifecycleOwner,
        private val func:()->Disposable
    )
    :LifecycleObserver {

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