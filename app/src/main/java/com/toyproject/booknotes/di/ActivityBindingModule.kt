package com.toyproject.booknotes.di

import com.toyproject.booknotes.di.ui.*
import com.toyproject.booknotes.ui.books.BookcaseActivity
import com.toyproject.booknotes.ui.barcode.BarcodeScanActivity
import com.toyproject.booknotes.ui.barcode.ZXingScannerActivity
import com.toyproject.booknotes.ui.detail.DetailBookInfoActivity
import com.toyproject.booknotes.ui.search.SearchBookActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(BookcaseModule::class))
    internal abstract fun bindBookcaseActivity(): BookcaseActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(SearchBookModule::class))
    internal abstract fun bindBookSearchActivity():SearchBookActivity


    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(BarcodeScanModule::class))
    internal abstract fun bindBarcodeScanActivity(): BarcodeScanActivity


    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(ZxingScannerModule::class))
    internal abstract fun bindBZxingScannerActivity(): ZXingScannerActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(DetailBookInfoModule::class))
    internal abstract fun bindDetailBookInfoActivity():DetailBookInfoActivity
}