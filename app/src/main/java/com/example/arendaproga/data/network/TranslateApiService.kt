package com.example.arendaproga.data.network

import retrofit2.http.GET
import retrofit2.http.Query

data class TranslateResponse(
    val responseData: ResponseData? = null,
    val responseStatus: Int = 0
)

data class ResponseData(
    val translatedText: String = ""
)

interface TranslateApiService {
    @GET("get")
    suspend fun translate(
        @Query("q") text: String,
        @Query("langpair") langPair: String = "en|ru"
    ): TranslateResponse
}