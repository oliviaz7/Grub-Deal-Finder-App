package com.example.grub.service

import com.example.grub.data.DealImageRequestBody
import com.example.grub.data.HandshakeResponse
import com.example.grub.data.auth.impl.ChangePasswordRequest
import com.example.grub.data.auth.impl.CreateUserRequest
import com.example.grub.data.auth.impl.LoginRequest
import com.example.grub.data.auth.impl.LoginResponse
import com.example.grub.data.deals.AddDealResponse
import com.example.grub.data.deals.ApiResponse
import com.example.grub.data.deals.GetRestaurantResponse
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.data.deals.AutoPopulateDealsResponse
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
        @Body createUserRequest: CreateUserRequest
    ): ApiResponse

    @POST("change_password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): ApiResponse

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @GET("search_nearby_restaurants")
    suspend fun searchNearbyRestaurants(
        @Query("keyword") keyword: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double
    ): List<RestaurantDealsResponse>

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

    @GET("get_restaurant")
    suspend fun getRestaurant(
        @Query("place_id") placeId: String
    ): GetRestaurantResponse

    @POST("proxy/generate")
    suspend fun autoPopulateDealFromImage(
        @Body request: DealImageRequestBody
    ): AutoPopulateDealsResponse

    @GET("proxy/handshake")
    suspend fun proxyHandshake(): HandshakeResponse

    @GET("get_user_by_id")
    suspend fun getUserById(
        @Query("user_id") userId: String
    ): LoginResponse

}

