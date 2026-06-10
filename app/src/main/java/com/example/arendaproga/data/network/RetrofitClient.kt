package com.example.arendaproga.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val client = OkHttpClient.Builder().build()

    val carApiService: CarApiService = Retrofit.Builder()
        .baseUrl("https://api.api-ninjas.com/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CarApiService::class.java)

    val translateApiService: TranslateApiService = Retrofit.Builder()
        .baseUrl("https://api.mymemory.translated.net/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TranslateApiService::class.java)
}