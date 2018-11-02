package com.toyproject.booknotes.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.util.Converters

@Database(entities = arrayOf(BookInfo::class), version = 1)
@TypeConverters(Converters::class)
abstract class BookInfoDatabase:RoomDatabase() {
    abstract fun bookInfoDao():BookInfoDao
}