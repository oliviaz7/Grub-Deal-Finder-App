package com.example.grub.service

import com.example.grub.data.deals.AddDealResponse
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.data.deals.RestaurantDealsResponse
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
    suspend fun addRestaurantDeal(@Body deal: RestaurantDealsResponse): AddDealResponse

    @GET("search_nearby_restaurants")
    suspend fun searchNearbyRestaurants(
        @Query("keyword") keyword: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double
    ): List<SimpleRestaurant>
}

