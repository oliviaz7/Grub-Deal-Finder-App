package com.example.grub.data.deals.impl

import android.util.Log
import com.example.grub.data.Result
import com.example.grub.data.deals.ApiResponse
import com.example.grub.data.deals.DealIdResponse
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.model.VoteType
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

    /**
     * This just combines the newDeals with the existing deal store.
     * Should be called with removeFarawayDeals to ensure that the deal store
     * is always relevant to the location of interest.
     */
    private fun updateAccumulatedDeals(newDeals: List<RestaurantDealsResponse>) {
        _accumulatedDeals.update { currentDeals ->
            (currentDeals + newDeals).distinctBy { it.id }
        }
    }

    private fun removeFarawayDeals(coordinates: LatLng, radius: Double) {
        _accumulatedDeals.update { currentDeals ->
            currentDeals.filter { deal ->
                val distance = FloatArray(1)
                android.location.Location.distanceBetween(
                    coordinates.latitude,
                    coordinates.longitude,
                    deal.coordinates.latitude,
                    deal.coordinates.longitude,
                    distance
                )
                distance[0] < radius
            }
        }
    }

    override suspend fun getRestaurantDeals(
        coordinates: LatLng,
        radius: Double
    ): Result<Unit> {
        return try {
            val latitude = coordinates.latitude
            val longitude = coordinates.longitude

            val response = apiService.getRestaurantDeals(latitude, longitude, radius)
            // add all the newly fetched deals to _accumulatedDeals
            updateAccumulatedDeals(response)

            // remove any deals that are too far away now (beyond 3x the radius)
            removeFarawayDeals(coordinates, radius * 3)

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("getRestaurantDeals", "Error fetching deals", e)
            Result.Error(e)
        }
    }

    override suspend fun addRestaurantDeal(deal: RestaurantDealsResponse): Result<DealIdResponse> {
        return try {
            val response = apiService.addRestaurantDeal(deal)
            Result.Success(response)
            // TODO: after adding a deal, optimistically add it accumulated deals
            // calling updateAccumulatedDeals (so it shows up right away without requiring
            // another fetch)

        } catch (e: Exception) {
            Log.e("addRestaurantDeal", "Error adding deal", e)
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
            Log.e("searchNearbyRestaurants", "Error searching restaurants", e)
            Result.Error(e)
        }
    }

    override suspend fun updateVote(
        dealId: String,
        userId: String,
        userVote: VoteType
    ): Result<ApiResponse> {
        return try {
            val response = apiService.updateVote(dealId, userId, userVote)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("updateVote", "Error updating vote", e)
            Result.Error(e)
        }
    }

    override suspend fun saveDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return try {
            val response = apiService.saveDeal(dealId, userId)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("saveDeal", "Error saving deal", e)
            Result.Error(e)
        }
    }

    override suspend fun unsaveDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return try {
            val response = apiService.unsaveDeal(dealId, userId)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("unsaveDeal", "Error unsaving deal", e)
            Result.Error(e)
        }
    }

    override suspend fun getSavedDeals(userId: String): Result<List<DealIdResponse>> {
        return try {
            val response = apiService.getSavedDeals(userId)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("getSavedDeals", "Error fetching saved deals", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return try {
            val response = apiService.deleteDeal(dealId, userId)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("deleteDeal", "Error deleting deal", e)
            Result.Error(e)
        }
    }
}
