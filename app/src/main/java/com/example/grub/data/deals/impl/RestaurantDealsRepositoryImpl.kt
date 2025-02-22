package com.example.grub.data.deals.impl

import com.example.grub.data.Result
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.service.RetrofitClient.apiService
import com.google.android.gms.maps.model.LatLng

class RestaurantDealsRepositoryImpl : RestaurantDealsRepository {
    override suspend fun getRestaurantDeals(
        coordinates: LatLng,
        radius: Double
    ): Result<List<RestaurantDealsResponse>> {
        return try {
            val latitude = coordinates.latitude
            val longitude = coordinates.longitude

            val response = apiService.getRestaurantDeals(latitude, longitude, radius)

            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
