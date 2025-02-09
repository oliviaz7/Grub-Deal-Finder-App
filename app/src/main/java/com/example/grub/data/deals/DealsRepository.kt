package com.example.grub.data.deals

import com.example.grub.data.Result
import com.example.grub.model.DealType
import com.google.android.gms.maps.model.LatLng

// RawDeal is the domain model. It mirrors the raw object returned by the server.
// It needs to be processed before we actually use it.
data class RawDeal(
    val id: String,
    val item: String,
    val description: String? = null,
    val type: DealType,
    val coordinates: LatLng,
    val placeId: String,
    val restaurantName: String,
    val expiryDate: Long? = null,
    val datePosted: Long,
    val userId: String,
    val restrictions: String, // TODO: figure out how we're handling
)

/**
 * Interface to the Deals data layer.
 */
interface DealsRepository {

    /**
     * Get deals based on user location and radius
     */
    suspend fun getDeals(coordinates: LatLng? = null, radius: Double = 3.0): Result<List<RawDeal>>
}