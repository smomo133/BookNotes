package com.toyproject.booknotes.ui.search

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import com.toyproject.booknotes.R
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.databinding.ActivityBookSearchBinding
import com.toyproject.booknotes.extension.plusAssign
import com.toyproject.booknotes.rx.AutoClearedDisposable
import com.toyproject.booknotes.ui.books.BookcaseActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.*
import java.util.*

@AndroidEntryPoint
class SearchBookActivity: AppCompatActivity(),
        SearchBookAdapter.ItemClickListener, SearchBookRecyclerView.LoadListener {
    internal lateinit var menuSearch:MenuItem
    internal lateinit var searchView: SearchView
    internal val disposable = AutoClearedDisposable(this)
    internal val viewDisposables
                = AutoClearedDisposable(this, false)

    private lateinit var searchBookAdapter : SearchBookAdapter
    private val viewModel:SearchBookViewModel by viewModels()

    private var isSearchListEnd:Boolean = true
    private var isSearchLoading:Boolean = false
    private var lastKeyword:String? = null
    private var isQueryTextChange:Boolean = false

    private lateinit var binding:ActivityBookSearchBinding

    override fun onItemClick(bookInfo: BookInfo) {
        alert{
            val addBook = getString(R.string.add_msg)
            message = String.format(addBook, bookInfo.title)
            yesButton {
                var date = Calendar.getInstance().time
                with(bookInfo){
                    readStartDate = date
                    readEndDate = date
                }
                disposable += viewModel.addToBookBasic(bookInfo)
                startActivity(intentFor<BookcaseActivity>().clearTop())
            }
            noButton { }
        }.show()
    }

    override fun onLoadNext() {
        if(isSearchLoading)   return
        if(viewModel.currentPage <= MAX_PAGE-1) {
            viewModel.currentPage++
        } else return
        //Log.d(TAG, "onLoadNext currentPage = ${viewModel.currentPage}")
        lastKeyword?.let {
            disposable += viewModel.addBookItems(it, viewModel.currentPage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookSearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.tbSearchBook)

        lifecycle += disposable
        lifecycle += viewDisposables

        searchBookAdapter = SearchBookAdapter()
        searchBookAdapter.setItemClickListener(this@SearchBookActivity)
        with(binding.rvActivitySearchList){
            layoutManager = LinearLayoutManager(this@SearchBookActivity)
            adapter = this@SearchBookActivity.searchBookAdapter
            setOnLoadListener(this@SearchBookActivity)
        }

        viewDisposables += viewModel.searchResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ documents ->
                    with(searchBookAdapter){
                        if(documents.isEmpty)   clearItems()
                        else {
                            if(isQueryTextChange){
                                clearItems()
                                isQueryTextChange = false
                            }
                            setItems(documents.value)
                        }
                    }
                }

        viewDisposables += viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { message ->
                    if(message.isEmpty) hideError()
                    else                showError(message.value)
                }

        viewDisposables += viewModel.isLoading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {isLoading ->
                    if(isLoading) {
                        showProgress()
                        isSearchLoading = true
                    } else {
                        hideProgress()
                        isSearchLoading = false
                    }
                }

        viewDisposables += viewModel.isListEnd
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isSearchListEnd = it
                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_book_search, menu)

        menuSearch = menu!!.findItem(R.id.menu_activity_search_query)
        searchView = menuSearch.actionView as SearchView

         viewDisposables += searchView.queryTextChangeEvents()
                 .filter { it.isSubmitted }
                 .map { it.queryText() }
                 .filter{ it.isNotEmpty()}
                 .map{ it.toString()}
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe {
                     query->
                        isQueryTextChange = true
                        updateTitle(query)
                        hideSoftKeyboard()
                        collapseSearchView()
                        searchBook(query, 1)
                 }

        viewDisposables += viewModel.lastSearchKeyword
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { keyword->
                    if(keyword.isEmpty) menuSearch.expandActionView()
                    else                lastKeyword = keyword.value
                }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(R.id.menu_activity_search_query == item.itemId){
            item.expandActionView()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showError(message:String?){
        with(binding.tvActivitySearchMessage){
            text = message ?: "Unexpected error"
            visibility = View.VISIBLE
        }
    }

    private fun hideError(){
        with(binding.tvActivitySearchMessage){
            text = ""
            visibility = View.GONE
        }
    }

    private fun showProgress(){
        binding.pbActivitySearch.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        binding.pbActivitySearch.visibility = View.GONE
    }

    private fun updateTitle(query:String){
        supportActionBar?.run{ subtitle = query}
    }

    private fun hideSoftKeyboard(){
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).run{
            hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    private fun collapseSearchView(){
        menuSearch.collapseActionView()
    }

    private fun searchBook(query: String, curPage:Int){
        disposable += viewModel.searchBook(query, curPage)
    }

    companion object {
        val TAG = "SearchBookActivity"
        const val MAX_PAGE:Int = 100
    }
}