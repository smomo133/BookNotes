package com.toyproject.booknotes.data

import androidx.room.*
import com.toyproject.booknotes.api.model.BookInfo
import io.reactivex.Flowable

@Dao
interface BookInfoDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(info:BookInfo)

    @Query("SELECT * FROM bookinfo")
    fun getAllBookList(): Flowable<List<BookInfo>>

    @Query("SELECT * FROM bookinfo WHERE title = :query LIMIT 1")
    fun getSearchBookList(query: String):Flowable<List<BookInfo>>

    @Query("DELETE FROM bookinfo")
    fun clearAll()

    @Update
    fun update(bookinfo: BookInfo)

    @Delete
    fun delete(bookinfo: BookInfo)

    @Delete
    fun deleteBookinfoList(list:List<BookInfo>)
}