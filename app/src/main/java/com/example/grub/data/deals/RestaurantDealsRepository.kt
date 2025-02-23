package com.example.grub.data.deals

import com.example.grub.data.Result
import com.example.grub.model.DealType
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

// Mirrors what we expect to receive from the server, unprocessed
data class RestaurantDealsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("place_id") val placeId: String,
    @SerializedName("coordinates") val coordinates: LatLng,
    @SerializedName("restaurant_name") val restaurantName: String,
    @SerializedName("Deal") val rawDeals: List<RawDeal>,
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
    @SerializedName("restrictions") val restrictions: String, // TODO: figure out how we're handling
    @SerializedName("image_id") val imageId: String?,
)

/**
 * Interface to the RestaurantDealsRepository data layer.
 */
interface RestaurantDealsRepository {

    /**
     * Get restaurant deals based on user location and radius
     * This returns the fatty-nested restaurant-deal object that we discussed
     */
    suspend fun getRestaurantDeals(
        coordinates: LatLng,
        radius: Double = 1000.0
    ): Result<List<RestaurantDealsResponse>>

    suspend fun addRestaurantDeal(deal: RestaurantDealsResponse): Result<Unit>
}