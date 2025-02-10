package com.example.grub.model

import com.google.android.gms.maps.model.LatLng
import java.time.ZonedDateTime

data class Deal(
    val id: String,
    val item: String,
    val description: String? = null,
    val type: DealType,
    val coordinates: LatLng,
    val placeId: String,
    val restaurantName: String,
    val expiryDate: ZonedDateTime? = null,
    val datePosted: ZonedDateTime,
    val userId: String,
    val restrictions: String, // TODO: figure out how we're handling
)

enum class DealType {
    BOGO,
    DISCOUNT,
    // TODO: add more
}
