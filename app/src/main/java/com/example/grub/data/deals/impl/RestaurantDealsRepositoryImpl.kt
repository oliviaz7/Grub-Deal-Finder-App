package com.example.grub.data.deals.impl

import com.example.grub.data.Result
import com.example.grub.data.deals.AddDealResponse
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.service.RetrofitClient.apiService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RestaurantDealsRepositoryImpl : RestaurantDealsRepository {

    private val _accumulatedDeals = MutableStateFlow<List<RestaurantDealsResponse>>(emptyList())
    override fun accumulatedDeals(): StateFlow<List<RestaurantDealsResponse>> =
        _accumulatedDeals.asStateFlow()

    private fun updateAccumulatedDeals(newDeals: List<RestaurantDealsResponse>) {
        _accumulatedDeals.update { currentDeals ->
            (currentDeals + newDeals).distinctBy { it.id }
        }
        // OLIVIA TODO: add logic to check that accumulatedDeals does not blow up in size
        // if _accumulatedDeals size exceeds a certain amount, add some logic to remove
        // some items from the list
    }

    override suspend fun getRestaurantDeals(
        coordinates: LatLng,
        radius: Double
    ): Result<Unit> {
        return try {
            val latitude = coordinates.latitude
            val longitude = coordinates.longitude

            val response = apiService.getRestaurantDeals(latitude, longitude, radius)
            updateAccumulatedDeals(response)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addRestaurantDeal(deal: RestaurantDealsResponse): Result<AddDealResponse> {
        return try {
            val response = apiService.addRestaurantDeal(deal)
            Result.Success(response)
            // TODO: after adding a deal, optimistically add it accumulated deals
            // calling updateAccumulatedDeals (so it shows up right away without requiring
            // another fetch)

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchNearbyRestaurants(
        keyword: String,
        coordinates: LatLng,
        radius: Double
    ): Result<List<SimpleRestaurant>> {
        return try {
            val latitude = coordinates.latitude
            val longitude = coordinates.longitude
            val response = apiService.searchNearbyRestaurants(keyword, latitude, longitude, radius)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
