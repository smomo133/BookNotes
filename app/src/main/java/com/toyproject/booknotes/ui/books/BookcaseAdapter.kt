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
import javax.inject.Inject

class BookcaseAdapter : RecyclerView.Adapter<BookcaseAdapter.ViewHolder>() {

    private var items:MutableList<BookInfo> = mutableListOf()
    private var listener: BookItemClickListener? = null
    private var toggleVisible:Int = View.GONE
    private lateinit var binding:ItemBookcaseListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemBookcaseListBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(items[position], listener, toggleVisible)
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

    class ViewHolder(private val _binding:ItemBookcaseListBinding)
        : RecyclerView.ViewHolder(_binding.root){
        fun bind(info:BookInfo, listener: BookItemClickListener?, isDeleteVisible:Int){
            Glide.with(_binding.root.context)
                    .load(info.thumbnail)
                    .into(_binding.ivBookcaseListThumb)
            _binding.tvBookcaseBookTitle.text = info.title
//            Log.d(TAG, "info.title = ${info.title}")
            info.authors?.let{
                _binding.tvBookcaseBookAuthors.text = TextUtil.commaEllipsize(it)
            }
            _binding.tvBookcaseBookPublisher.text = info.publisher
            _binding.ratingBookcaseGrade.rating = info.grade
            _binding.tbBookcaseListDelete.setOnCheckedChangeListener { compoundButton, b ->
                listener?.onItemSelected(info, b)
            }
            _binding.tbBookcaseListDelete.visibility = isDeleteVisible
            _binding.root.setOnClickListener {
                listener?.onItemClick(info)
            }
        }
    }

}