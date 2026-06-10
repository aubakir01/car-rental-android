package com.example.arendaproga.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class CarApiResponse(
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val cylinders: Int? = null,
    val fuel_type: String? = null,
    val transmission: String? = null,
    val drive: String? = null,
    val displacement: Double? = null,
    val `class`: String? = null
)
interface CarApiService {
    @GET("cars")
    suspend fun getCars(
        @Header("X-Api-Key") apiKey: String,
        @Query("make") make: String,
        @Query("year") year: Int = 2020
    ): List<CarApiResponse>
}

