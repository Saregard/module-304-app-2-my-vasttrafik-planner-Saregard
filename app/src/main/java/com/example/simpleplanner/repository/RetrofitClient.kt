package com.example.simpleplanner.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val baseUrl = "https://api.vasttrafik.se/bin/rest.exe/v2/"
    val instance: Endpoint by lazy {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
        retrofit.create(Endpoint::class.java)
    }
}