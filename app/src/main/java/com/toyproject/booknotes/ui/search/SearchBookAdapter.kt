package com.toyproject.booknotes.ui.search


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
        //holder.bind(items[position])
        items[position].let { info ->
            with(holder.itemView){
                    Glide
                            .with(context)
                            .load(info.thumbnail)
                            .into(binding.ivItemBookThumb)

                binding.tvBookTitle.text = info.title
                binding.tvAuthors.setText("")

                info.authors?.let{
                    binding.tvAuthors.text = TextUtil.commaEllipsize(it)
                }
                binding.tvPublisher.text = info.publisher
                info.datetime?.let{binding.tvDatetime.text = TextUtil.convertISOFormatDateStr(info.datetime)}
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

    class BookHolder(private val binding: ItemSearchBookBinding)
        : RecyclerView.ViewHolder(binding.root){
    }

    interface ItemClickListener{
        fun onItemClick(bookInfo:BookInfo)
    }

    companion object {
        val TAG = "SearchBookAdapter"
    }
}