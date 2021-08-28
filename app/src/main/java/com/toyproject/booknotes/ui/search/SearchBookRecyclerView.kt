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

    private var listener:LoadListener? = null

    companion object {
        val TAG = "SearchRecyclerView"
        const val VISIBLE_THRESHOLD = 7
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        if(dy < 0)  return
        val layoutManager = layoutManager as LinearLayoutManager
        var visibleItemCount = this.childCount
        var totalItemCount = layoutManager.itemCount
        var firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

        if((totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + VISIBLE_THRESHOLD)){
            listener?.onLoadNext()
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
    }

    fun setOnLoadListener(listener:LoadListener?){
        this.listener = listener
    }

    interface LoadListener{
        fun onLoadNext()
    }
}