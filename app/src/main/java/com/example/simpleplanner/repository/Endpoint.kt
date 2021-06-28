package com.example.simpleplanner.repository

import com.example.simpleplanner.models.StopData
import com.example.simpleplanner.util.Credential
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface Endpoint {

    @Headers("Authorization: ${Credential.VASTRAFIK_KEY}")
    @GET("location.nearbystops")
    fun getNearbyStops(
        @Query("originCoordLat") latitude: Double,
        @Query("originCoordLong") longitude: Double,
        @Query("format") format: String = "json"
    ): Call<StopData>
}