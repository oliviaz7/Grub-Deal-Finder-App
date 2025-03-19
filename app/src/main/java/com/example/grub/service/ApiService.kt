package com.example.grub.service

import com.example.grub.data.auth.impl.LoginResponse
import com.example.grub.data.deals.ApiResponse
import com.example.grub.data.deals.AddDealResponse
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.model.VoteType
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("restaurant_deals")
    suspend fun getRestaurantDeals(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double,
        @Query("user_id") userId: String? = null
    ): List<RestaurantDealsResponse>

    @POST("add_restaurant_deal")
    suspend fun addRestaurantDeal(
        @Body deal: RestaurantDealsResponse
    ): AddDealResponse

    @POST("create_new_user_account")
    suspend fun createNewUserAccount(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("firstName") firstName: String,
        @Query("lastName") lastName: String,
        @Query("email") email: String
    ): ApiResponse

    @GET("login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String,
    ): LoginResponse

    @GET("search_nearby_restaurants")
    suspend fun searchNearbyRestaurants(
        @Query("keyword") keyword: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double
    ): List<SimpleRestaurant>

    @GET("update_vote")
    suspend fun updateVote(
        @Query("deal_id") dealId: String,
        @Query("user_id") userId: String,
        @Query("user_vote") userVote: VoteType
    ): ApiResponse

    @GET("save_deal")
    suspend fun saveDeal(
        @Query("deal_id") dealId: String,
        @Query("user_id") userId: String
    ): ApiResponse

    @GET("unsave_deal")
    suspend fun unsaveDeal(
        @Query("deal_id") dealId: String,
        @Query("user_id") userId: String
    ): ApiResponse

    @GET("get_saved_deals")
    suspend fun getSavedDeals(
        @Query("user_id") userId: String
    ): List<RestaurantDealsResponse>

    @GET("delete_deal")
    suspend fun deleteDeal(
        @Query("deal_id") dealId: String,
        @Query("user_id") userId: String
    ): ApiResponse
}

