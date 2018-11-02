package com.toyproject.booknotes.util

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import io.reactivex.annotations.NonNull
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Converters {
    //requires api level 26
    //private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun listToJson(value : Array<String>?):String{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value : String):Array<String>?{
        val objects = Gson().fromJson(value, Array<String>::class.java)
        return objects
    }

    @TypeConverter
    fun fromTimestamp(value:Long?): Date {
        return value?.let{Date(value)} ?:Date()
    }

    @TypeConverter
    fun dateToTimestamp(date:Date?):Long?{
        return date?.let { date.time } ?:0
    }
}