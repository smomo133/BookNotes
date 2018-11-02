package com.toyproject.booknotes.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object TextUtil {
    fun commaEllipsize(value:Array<String>?):String{
       var str:String = ""
        value!!.forEachIndexed { index, s ->
            if(index != value.size - 1) str += s + " , "
            else                        str += s
        }
        return str
    }

    /**
     * ISO 8601
     */
    fun convertISOFormatDateStr(dateTime:String):String{
        if(dateTime != "") {
            val cal = Calendar.getInstance()
            cal.setTime(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateTime))
            return SimpleDateFormat("yyyy년 MM월 dd일").format(cal.time)
        } else {
            return dateTime
        }
    }

    fun convertDateToStr(date:Date):String{
        return SimpleDateFormat("yyyy년 MM월 dd일").format(date)
    }
}