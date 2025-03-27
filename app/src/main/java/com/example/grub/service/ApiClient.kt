package com.example.grub.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.grub.service.ApiService

object RetrofitClient {
    // railway: "http://grub-production.up.railway.app"
    private const val BASE_URL = "http://10.0.2.2:5001/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}