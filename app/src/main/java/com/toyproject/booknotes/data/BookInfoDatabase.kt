package com.toyproject.booknotes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.util.Converters

@Database(entities = arrayOf(BookInfo::class), version = 1)
@TypeConverters(Converters::class)
abstract class BookInfoDatabase: RoomDatabase() {
    abstract fun bookInfoDao():BookInfoDao
}