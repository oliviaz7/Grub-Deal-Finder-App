package com.example.grub.model

import com.google.android.gms.maps.model.LatLng

data class RestaurantDeal (
    val id: String,
    val placeId: String,
    val restaurantName: String,
    val coordinates: LatLng,
    val deals: List<Deal>,
)