package com.toyproject.booknotes.api.model

import com.google.gson.annotations.SerializedName

class BookSearchResponse(
        val documents: List<BookInfo>,
        val meta:Meta)

data class Meta(
        @SerializedName("total_count") val totalCount:Int,
        @SerializedName("pageable_count") val pageableCount:Int,
        @SerializedName("is_end") val isEnd:Boolean)

