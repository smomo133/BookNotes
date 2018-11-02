package com.toyproject.booknotes.ui.search

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.toyproject.booknotes.R
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.util.TextUtil
import kotlinx.android.synthetic.main.item_search_book.view.*
import java.util.*

class SearchBookAdapter :RecyclerView.Adapter<SearchBookAdapter.BookHolder>(){

    private var items:MutableList<BookInfo> = mutableListOf()
    private var listener : ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder
        = BookHolder(parent)

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        items[position].let { info ->
            with(holder.itemView){
                    Glide
                            .with(context)
                            .load(info.thumbnail)
                            .into(ivItemBookThumb)

                tvBookTitle.text = info.title
                tvAuthors.setText("")

                info.authors?.let{
                    tvAuthors.text = TextUtil.commaEllipsize(it)
                }
                tvPublisher.text = info.publisher
                info.datetime?.let{tvDatetime.text = TextUtil.convertISOFormatDateStr(info.datetime)}
                setOnClickListener { listener?.onItemClick(info) }
            }
        }
    }

    override fun getItemCount() = items.size

    fun getItems():MutableList<BookInfo> = items

    fun setItems(items:List<BookInfo>){
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    fun addItems(items:List<BookInfo>){
        //val positionStart:Int = this.items.size
        clearItems()
        this.items.addAll(items.toMutableList())
        //notifyItemRangeInserted(positionStart, this.items.size)
        notifyDataSetChanged()
    }

    fun setItemClickListener(listener:ItemClickListener?){
        this.listener = listener
    }

    fun clearItems(){
        this.items.clear()
        notifyDataSetChanged()
    }

    class BookHolder(parent:ViewGroup): RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_search_book,parent,false))

    interface ItemClickListener{
        fun onItemClick(bookInfo:BookInfo)
    }


    companion object {
        val TAG = "SearchBookAdapter"
    }
}