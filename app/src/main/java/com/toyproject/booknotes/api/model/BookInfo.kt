package com.toyproject.booknotes.api.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(tableName = "bookinfo")
class BookInfo (
        val authors:Array<String>?,
        @ColumnInfo(collate = ColumnInfo.NOCASE) val title: String,
        //val barcode: String?,
        //val category: String?,
        val contents: String?,
        val datetime: String?,
        //@SerializedName("ebook_barcode") val ebookBarcode:String?,
        @PrimaryKey @ColumnInfo(name="isbn") val isbn:String,
        val price:Int?,
        val publisher:String?,
        @SerializedName("sale_price") val salePrice:Int?,
        //@SerializedName("sale_yn") val saleYn:String?,
        val status:String?,
        val thumbnail:String?,
        val translators:Array<String>?,
        val url:String?,
        val isReadingStart:Boolean,
        var readStartDate: Date,
        var readEndDate: Date,
        var bookType:Int = 0,   //0 = paper, 1 = ebook, 2 = etc
        val isPossess:Boolean?,
        var grade:Float = 0.0f,
        var review:String?,
        val memo:String?,
        val tag:Array<String>?,
        val caseType:String?,
        var purchase:String?
        ) : Serializable