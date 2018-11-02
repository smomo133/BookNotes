package com.toyproject.booknotes.di

import android.app.Application
import com.toyproject.booknotes.BookNoteApplication
import com.toyproject.booknotes.di.ui.LocalDataModule

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        ApiModule::class,
        NetworkModule::class,
        LocalDataModule::class,
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class
        ))

interface AppComponent :AndroidInjector<BookNoteApplication> {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(app:Application):Builder

        fun build():AppComponent
    }
}