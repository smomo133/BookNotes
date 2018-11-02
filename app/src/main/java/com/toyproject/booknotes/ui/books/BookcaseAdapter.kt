package com.toyproject.booknotes.ui.books

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.toyproject.booknotes.R
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.util.TextUtil
import kotlinx.android.synthetic.main.item_bookcase_list.view.*


class BookcaseAdapter: RecyclerView.Adapter<BookcaseAdapter.ViewHolder>() {

    private var items:MutableList<BookInfo> = mutableListOf()
    private var listener: BookItemClickListener? = null
    private var toggleVisible:Int = View.GONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
         = ViewHolder(parent)

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        items[position].let{
            info->
            with(viewHolder.itemView){
                Glide.with(context)
                        .load(info.thumbnail)
                        .into(ivBookcaseListThumb)
                tvBookcaseBookTitle.text = info.title
                info.authors?.let{
                    tvBookcaseBookAuthors.text = TextUtil.commaEllipsize(it)
                }
                tvBookcaseBookPublisher.text = info.publisher
                ratingBookcaseGrade.rating = info.grade
                tbBookcaseListDelete.setOnCheckedChangeListener { compoundButton, b ->
                    listener?.onItemSelected(info, b)
                }
                tbBookcaseListDelete.visibility = toggleVisible
                setOnClickListener {
                    listener?.onItemClick(info)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItemClickListener(listener:BookItemClickListener){
        this.listener = listener
    }

    interface BookItemClickListener{
        fun onItemClick(bookInfo:BookInfo)
        fun onItemSelected(bookInfo: BookInfo, isSlected:Boolean)
    }

    class ViewHolder(parent: ViewGroup):RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_bookcase_list, parent, false))


    fun setItems(items:List<BookInfo>){
        this.items = items.toMutableList()
    }

    fun getItems():List<BookInfo>{
        return items
    }

    fun clearItems(){
        this.items.clear()
    }

    fun setToggleVisible(visiblity:Int){
        toggleVisible = visiblity
        notifyDataSetChanged()
    }

    companion object {
        private val TAG = "BookcaseAdapter"
        const val LIST = 0
        const val GRID = 1
    }
}