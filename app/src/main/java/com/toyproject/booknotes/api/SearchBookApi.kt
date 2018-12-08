package com.toyproject.booknotes.api

import com.toyproject.booknotes.api.model.BookSearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface SearchBookApi {
    @Headers("Authorization: KakaoAK {app_key}")
    @GET("/v3/search/book")
    fun searchBook(@Query("query") query: String,
                   @Query("page") page:Int = 1,
                   @Query("size") size:Int = 10 ): Observable<BookSearchResponse>
}