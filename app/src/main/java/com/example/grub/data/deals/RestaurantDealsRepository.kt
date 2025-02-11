package com.example.grub.data.deals

import com.example.grub.data.Result
import com.example.grub.model.DealType
import com.google.android.gms.maps.model.LatLng

// Mirrors what we expect to receive from the server, unprocessed
data class RestaurantDealResponse(
    val id: String,
    val placeId: String,
    val coordinates: LatLng,
    val restaurantName: String,
    val rawDeal: List<RawDeal>,
)

// RawDeal is the domain model. It mirrors the raw object returned by the server.
// It needs to be processed before we actually use it.
data class RawDeal(
    val id: String,
    val item: String,
    val description: String? = null,
    val type: DealType,
    val expiryDate: Long? = null,
    val datePosted: Long,
    val userId: String,
    val restrictions: String, // TODO: figure out how we're handling
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
        coordinates: LatLng? = null,
        radius: Double = 3.0
    ): Result<List<RestaurantDealResponse>>
}