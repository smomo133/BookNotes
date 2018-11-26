package com.toyproject.booknotes.ui.search

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import dagger.android.support.DaggerAppCompatActivity
import com.toyproject.booknotes.R
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.extension.plusAssign
import com.toyproject.booknotes.rx.AutoClearedDisposable
import com.toyproject.booknotes.ui.books.BookcaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_book_search.*
import org.jetbrains.anko.*
import java.util.*
import javax.inject.Inject

class SearchBookActivity:DaggerAppCompatActivity(),
        SearchBookAdapter.ItemClickListener, SearchBookRecyclerView.LoadListener {
    internal lateinit var menuSearch:MenuItem
    internal lateinit var searchView:SearchView
    internal val disposable = AutoClearedDisposable(this)
    internal val viewDisposables
                = AutoClearedDisposable(this, false)
    lateinit var viewModel:SearchBookViewModel
    @Inject lateinit var viewModelFactory: SearchBookViewModelFactory
    @Inject lateinit var adapter: SearchBookAdapter

    private var isSearchListEnd:Boolean = true
    private var isLoadNext:Boolean = false
    private var isLoading:Boolean = false
    private var lastKeyword:String? = null

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

    override fun onLoadNext(curPage: Int) {
        isLoadNext = true
        lastKeyword?.let {
            disposable += viewModel.addBookItems(it, curPage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)
        setSupportActionBar(tbSearchBook)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SearchBookViewModel::class.java]

        lifecycle += disposable
        lifecycle += viewDisposables

        with(rvActivitySearchList){
            layoutManager = LinearLayoutManager(this@SearchBookActivity)
            adapter = this@SearchBookActivity.adapter
            setOnLoadListener(this@SearchBookActivity)
        }

        viewDisposables += viewModel.searchResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ documents ->
                    with(adapter){
                        if(documents.isEmpty)   clearItems()
                        else{
                            //setItems(documents.value)
                            if(!isLoadNext)     setItems(documents.value)
                            else {
                                addItems(documents.value)
                                rvActivitySearchList.isLoading = false
                            }
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
                    if(isLoading)
                        showProgress()
                    else
                        hideProgress()
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
                        isLoadNext = false
                        updateTitle(query)
                        hideSoftKeyboard()
                        collapseSearchView()
                        searchBook(query, 1)
                 }

        viewDisposables += viewModel.lastSearchKeyword
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { keyword->
                    if(keyword.isEmpty) menuSearch.expandActionView()
                    else {
                        lastKeyword = keyword.value
                        updateTitle(keyword.value)
                    }
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
        with(tvActivitySearchMessage){
            text = message ?: "Unexpected error"
            visibility = View.VISIBLE
        }
    }

    private fun hideError(){
        with(tvActivitySearchMessage){
            text = ""
            visibility = View.GONE
        }
    }

    private fun showProgress(){
        pbActivitySearch.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        pbActivitySearch.visibility = View.GONE
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
    }
}