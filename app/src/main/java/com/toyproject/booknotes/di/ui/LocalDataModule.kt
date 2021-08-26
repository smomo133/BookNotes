package com.toyproject.booknotes.di.ui

import android.content.Context
import androidx.room.Room
import com.toyproject.booknotes.data.BookInfoDao
import com.toyproject.booknotes.data.BookInfoDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class LocalDataModule {

    @Provides
    @Singleton
    fun provideBookInfoDao(db:BookInfoDatabase):BookInfoDao
            = db.bookInfoDao()

    @Provides
    @Singleton
    fun provideDatabase(@Named("appContext")context: Context):BookInfoDatabase
            = Room.databaseBuilder(context,
                BookInfoDatabase::class.java, "book_info.db").build()
}