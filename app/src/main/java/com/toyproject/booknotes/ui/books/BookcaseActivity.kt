package com.toyproject.booknotes.ui.books

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.toyproject.booknotes.R
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.databinding.ActivityBookcaseBinding
import com.toyproject.booknotes.extension.plusAssign
import com.toyproject.booknotes.rx.AutoActivateDisposable
import com.toyproject.booknotes.rx.AutoClearedDisposable
import com.toyproject.booknotes.ui.barcode.ZXingScannerActivity
import com.toyproject.booknotes.ui.detail.DetailBookInfoActivity
import com.toyproject.booknotes.ui.search.SearchBookActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*

@AndroidEntryPoint
class BookcaseActivity : AppCompatActivity(), AnkoLogger, BookcaseAdapter.BookItemClickListener{

    private lateinit var menuSearch: MenuItem
    private lateinit var searchView: SearchView

    private lateinit var binding:ActivityBookcaseBinding

    private var checkItems:MutableList<BookInfo> = mutableListOf()
    private var isEditOpen:Boolean = false

    private val disposables = AutoClearedDisposable(this)
    private val viewDisposables = AutoClearedDisposable(this, false)

    private val viewModel:BookcaseViewModel by viewModels()
    private lateinit var bookcaseAdapter: BookcaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookcaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbBookcaseTop)

        lifecycle += disposables
        lifecycle += viewDisposables

        loadBooklist()

        bookcaseAdapter = BookcaseAdapter()
        bookcaseAdapter.setItemClickListener(this@BookcaseActivity)
        with(binding.rvActvityBookcaseList){
            layoutManager = LinearLayoutManager(this@BookcaseActivity)
            adapter = this@BookcaseActivity.bookcaseAdapter
        }

        viewDisposables += viewModel.searchResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ documents ->
                    with(bookcaseAdapter){
                        if(documents.isEmpty)   clearItems()
                        else                    setItems(documents.value)
                        notifyDataSetChanged()
                    }
                }

        viewDisposables += viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    msg ->
                    if(msg.isEmpty) hideMessage()
                    else            showMessage(msg.value)
                }

    }

    override fun onItemClick(bookInfo: BookInfo) {
        if(!isEditOpen)
            startActivity<DetailBookInfoActivity>(DetailBookInfoActivity.BOOK_INFO to bookInfo)
    }

    override fun onItemSelected(bookInfo: BookInfo, isSlected: Boolean) {
        if(isSlected)   checkItems.add(bookInfo)
        else            checkItems.remove(bookInfo)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_bookcase, menu)

        menu?.findItem(R.id.menu_activity_bookcase_add).actionView.setOnClickListener {
            showAddBookMenu(it)
        }

        menu?.findItem(R.id.menu_activity_bookcase_edit).setOnMenuItemClickListener {
            isEditOpen = true
            editStateChanged(menu)
            true
        }

        menu?.findItem(R.id.menu_activity_bookcase_delete).setOnMenuItemClickListener {
            isEditOpen = false
            editStateChanged(menu)
            viewDisposables += viewModel.removeBookInfoList(checkItems)
            true
        }

        menu?.findItem(R.id.menu_activity_bookcase_edit_done).setOnMenuItemClickListener{
            isEditOpen = false
            editStateChanged(menu)
            true
        }

        menu?.findItem(R.id.menu_activity_bookcase_search_cancel).setOnMenuItemClickListener {
            loadBooklist()
            menu.setGroupVisible(R.id.group_menu_book_case, true)
            menu.setGroupVisible(R.id.group_menu_search_result, false)
            true
        }
        return true
    }

    private fun loadBooklist(){
        lifecycle += AutoActivateDisposable(this){
            viewModel.bookcaseList
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { items->
                        with(bookcaseAdapter){
                            if(items.isEmpty)   clearItems()
                            else                setItems(items.value)
                            notifyDataSetChanged()
                        }
                        updateTitle(resources.getString(R.string.basic_book_case))
                    }
        }
    }

    private fun editStateChanged(menu:Menu){
        var isEditVisible:Int = View.VISIBLE
        if(!isEditOpen)     isEditVisible = View.GONE
        menu.setGroupVisible(R.id.group_menu_edit, isEditOpen)
        menu.setGroupVisible(R.id.group_menu_book_case, !isEditOpen)
        //menuSearch?.run{setVisible(!isEditOpen)}
        bookcaseAdapter.setToggleVisible(isEditVisible)
    }

    private fun showMessage(message:String?){
        with(binding.tvActivityBookMessage){
            text = message ?: "Unexpected error"
            visibility = View.VISIBLE
        }
    }

    private fun hideMessage(){
        with(binding.tvActivityBookMessage){
            text = ""
            visibility = View.GONE
        }
    }

    fun searchBooks(query:String){
        disposables += viewModel.searchBook(query)
    }

    private fun hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun updateTitle(caseTitle:String?){
        var numOfBook:Int = bookcaseAdapter.itemCount
        var title:String? = caseTitle + "(" + numOfBook + ")"
        supportActionBar?.setTitle(title)
    }

    fun showAddBookMenu(view:View){
        val popupMenu = PopupMenu(this@BookcaseActivity, view)
        popupMenu.inflate(R.menu.menu_activity_bookcase_add_book)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when(it!!.itemId) {
                R.id.menu_add_search -> {
                    startActivity<SearchBookActivity>()
                }
                R.id.menu_add_barcode->{
                    //startActivity<BarcodeScanActivity>()
                    startActivity<ZXingScannerActivity>()
                }
                //R.id.menu_add_direct->{}
            }
            true
        })
        popupMenu.show()
    }

    companion object {
    val TAG = "BookcaseActivity"
    }
}
