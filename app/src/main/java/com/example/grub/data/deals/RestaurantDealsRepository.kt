package com.example.grub.data.deals

import com.example.grub.data.Result
import com.example.grub.model.ApplicableGroup
import com.example.grub.model.DealType
import com.example.grub.model.VoteType
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.StateFlow

// Mirrors what we expect to receive from the server, unprocessed
data class RestaurantDealsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("place_id") val placeId: String,
    @SerializedName("coordinates") val coordinates: LatLng,
    @SerializedName("restaurant_name") val restaurantName: String,
    @SerializedName("display_address") val displayAddress: String,
    @SerializedName("Deal") val rawDeals: List<RawDeal>,
    @SerializedName("image_url") val imageUrl : String? = null,
)

// RawDeal is the domain model. It mirrors the raw object returned by the server.
// It needs to be processed before we actually use it.
data class RawDeal(
    @SerializedName("id") val id: String,
    @SerializedName("item") val item: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("type") val type: DealType,
    @SerializedName("expiry_date") val expiryDate: Long? = null,
    @SerializedName("date_posted") val datePosted: Long,
    @SerializedName("user_id") val userId: String,
    @SerializedName("username") val username: String? = "", // TODO: remove "" after implementing in FE
    @SerializedName("image_id") val imageId: String?,
    @SerializedName("user_saved") val userSaved: Boolean = false,
    @SerializedName("user_vote") val userVote: VoteType? = VoteType.NEUTRAL,
    @SerializedName("applicable_group") val applicableGroup: ApplicableGroup = ApplicableGroup.NONE,
    @SerializedName("daily_start_times") val dailyStartTimes: List<Int>? = null,
    @SerializedName("daily_end_times") val dailyEndTimes: List<Int>? = null,
    @SerializedName("num_upvote") val numUpvote: Int = 0,
    @SerializedName("num_downvote") val numDownvote: Int = 0,
    @SerializedName("price") val price: Double = 0.0,
)

data class AddDealResponse(
    val dealId: String
)

data class GetRestaurantResponse(
    val restaurant : RestaurantDealsResponse?
)

// for generic response
data class ApiResponse(
    val success: Boolean,
    val message: String
)

/**
 * Interface to the RestaurantDealsRepository data layer.
 */
interface RestaurantDealsRepository {
    /**
     * Reactive paradigm
     * getRestaurantDeals acts as a publisher (triggers updates to _accumulatedDeals),
     * and accumulatedDeals() is the subscription point for consumers.
     */
    fun accumulatedDeals(): StateFlow<List<RestaurantDealsResponse>>

    /**
     * Get restaurant deals based on user location and radius
     * This returns the fatty-nested restaurant-deal object that we discussed
     */
    suspend fun getRestaurantDeals(
        coordinates: LatLng,
        radius: Double = 1000.0,
        userId: String?
    ): Result<Unit>

     /**
     * Add a new restaurant deal
     */
    suspend fun addRestaurantDeal(deal: RestaurantDealsResponse): Result<AddDealResponse>

    /**
     * searchNearbyRestaurants
     */
    suspend fun searchNearbyRestaurants(
        keyword: String,
        coordinates: LatLng,
        radius: Double = 1000.0
    ): Result<List<RestaurantDealsResponse>>

    suspend fun updateVote(
        dealId: String,
        userId: String,
        userVote: VoteType
    ): Result<ApiResponse>

    suspend fun saveDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse>

    suspend fun unsaveDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse>

    suspend fun getSavedDeals(
        userId: String
    ): Result<List<RestaurantDealsResponse>>

    suspend fun deleteDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse>

    suspend fun getRestaurant(
        placeId: String,
    ) : Result<GetRestaurantResponse>
}