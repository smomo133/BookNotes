package com.toyproject.booknotes.ui.search

import android.content.Context

import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyproject.booknotes.api.model.BookInfo

class SearchBookRecyclerView @JvmOverloads
constructor(context:Context, attrs:AttributeSet?=null, defStyleAtt:Int = 0)
    : RecyclerView(context, attrs, defStyleAtt){

    internal var isLoading:Boolean = false

    private var currentPage:Int = 1
    private var listener:LoadListener? = null
    private var previousTotal = 0

    companion object {
        val TAG = "SearchRecyclerView"
        const val visibleThreshold = 4
        const val maxPage:Int = 100
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        if(dy < 0)  return
        val layoutManager = layoutManager as LinearLayoutManager
        var visibleItemCount = this.childCount
        var totalItemCount = layoutManager.itemCount
        var firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        if(!isLoading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)){
            isLoading = true
            if(currentPage < maxPage - 1){
                currentPage++
                listener?.onLoadNext(currentPage)
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
    }

    fun setOnLoadListener(listener:LoadListener?){
        this.listener = listener
    }

    interface LoadListener{
        fun onLoadNext(curPage:Int)
    }
}