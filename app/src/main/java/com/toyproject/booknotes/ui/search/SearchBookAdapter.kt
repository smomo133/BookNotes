package com.toyproject.booknotes.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.databinding.ItemSearchBookBinding
import com.toyproject.booknotes.util.TextUtil

class SearchBookAdapter : RecyclerView.Adapter<SearchBookAdapter.BookHolder>(){

    private var items:MutableList<BookInfo> = mutableListOf()
    private var listener : ItemClickListener? = null
    private lateinit var binding:ItemSearchBookBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder{
        binding = ItemSearchBookBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return BookHolder(binding)
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount() = items.size

    fun getItems():MutableList<BookInfo> = items

    fun setItems(item_list:List<BookInfo>){
        this.items.addAll(item_list.toMutableList())
        notifyDataSetChanged()
    }

    fun addItems(items:List<BookInfo>){
        clearItems()
        this.items.addAll(items.toMutableList())
        notifyDataSetChanged()
    }

    fun setItemClickListener(listener:ItemClickListener?){
        this.listener = listener
    }

    fun clearItems(){
        this.items.clear()
        notifyDataSetChanged()
    }

    class BookHolder(private val _binding: ItemSearchBookBinding)
        : RecyclerView.ViewHolder(_binding.root){
        fun bind(info:BookInfo, listener: ItemClickListener?){
            Glide
                .with(_binding.root.context)
                .load(info.thumbnail)
                .into(_binding.ivItemBookThumb)
            _binding.tvBookTitle.text = info.title
            _binding.tvAuthors.setText("")

            info.authors?.let{
                _binding.tvAuthors.text = TextUtil.commaEllipsize(it)
            }
            _binding.tvPublisher.text = info.publisher
            info.datetime?.let{_binding.tvDatetime.text = TextUtil.convertISOFormatDateStr(info.datetime)}
            _binding.root.setOnClickListener { listener?.onItemClick(info) }
        }
    }

    interface ItemClickListener{
        fun onItemClick(bookInfo:BookInfo)
    }

    companion object {
        val TAG = "SearchBookAdapter"
    }
}