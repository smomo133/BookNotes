package com.toyproject.booknotes.ui.books

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.databinding.ItemBookcaseListBinding
import com.toyproject.booknotes.util.TextUtil

class BookcaseAdapter: RecyclerView.Adapter<BookcaseAdapter.ViewHolder>() {

    private var items:MutableList<BookInfo> = mutableListOf()
    private var listener: BookItemClickListener? = null
    private var toggleVisible:Int = View.GONE
    private lateinit var binding:ItemBookcaseListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemBookcaseListBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        items[position].let{
            info->
            with(viewHolder.itemView){
                Glide.with(context)
                        .load(info.thumbnail)
                        .into(binding.ivBookcaseListThumb)
                binding.tvBookcaseBookTitle.text = info.title
                info.authors?.let{
                    binding.tvBookcaseBookAuthors.text = TextUtil.commaEllipsize(it)
                }
                binding.tvBookcaseBookPublisher.text = info.publisher
                binding.ratingBookcaseGrade.rating = info.grade
                binding.tbBookcaseListDelete.setOnCheckedChangeListener { compoundButton, b ->
                    listener?.onItemSelected(info, b)
                }
                binding.tbBookcaseListDelete.visibility = toggleVisible
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

    class ViewHolder(private val binding:ItemBookcaseListBinding):RecyclerView.ViewHolder(binding.root){

    }

}