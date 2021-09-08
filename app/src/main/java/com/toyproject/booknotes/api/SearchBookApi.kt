package com.toyproject.booknotes.api

import com.toyproject.booknotes.api.model.BookSearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface SearchBookApi {
    @Headers("Authorization: KakaoAK b4b94d41f5977178dd70aedd0aa8b0a9")
    @GET("/v3/search/book")
    fun searchBook(@Query("query") query: String,
                   @Query("page") page:Int = 1,
                   @Query("size") size:Int = 10 ): Observable<BookSearchResponse>
}