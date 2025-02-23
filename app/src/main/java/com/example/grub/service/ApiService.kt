package com.example.grub.service

import com.example.grub.data.deals.RestaurantDealsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("restaurant_deals")
    suspend fun getRestaurantDeals(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double
    ): List<RestaurantDealsResponse>

    @POST("add_restaurant_deal")
    suspend fun addRestaurantDeal(@Body deal: RestaurantDealsResponse): Response<Unit>
}

