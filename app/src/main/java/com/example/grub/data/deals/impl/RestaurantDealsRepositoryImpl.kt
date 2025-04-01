package com.example.grub.data.deals.impl

import android.util.Log
import com.example.grub.data.Result
import com.example.grub.data.deals.AddDealResponse
import com.example.grub.data.deals.ApiResponse
import com.example.grub.data.deals.GetRestaurantResponse
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
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
    private fun updateAccumulatedDealsWithVote(newDeals: List<RestaurantDealsResponse>) {
        _accumulatedDeals.update { currentRestaurantDeals ->
            newDeals + currentRestaurantDeals.filter { restaurant ->
                newDeals.none { it.id == restaurant.id }
            }
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
        radius: Double,
        userId: String?
    ): Result<Unit> {
        return try {
            val latitude = coordinates.latitude
            val longitude = coordinates.longitude

            val response = apiService.getRestaurantDeals(latitude, longitude, radius, userId)
            // add all the newly fetched deals to _accumulatedDeals
            updateAccumulatedDealsWithVote(response)

            // remove any deals that are too far away now (beyond 3x the radius)
            removeFarawayDeals(coordinates, radius * 3)

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("RestaurantDealsError", "Error fetching deals", e)
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
            Log.e("RestaurantDealsError", "Error adding deal", e)
            Result.Error(e)
        }
    }

    override suspend fun searchNearbyRestaurants(
        keyword: String,
        coordinates: LatLng,
        radius: Double
    ): Result<List<RestaurantDealsResponse>> {
        return try {
            val latitude = coordinates.latitude
            val longitude = coordinates.longitude
            val response = apiService.searchNearbyRestaurants(keyword, latitude, longitude, radius)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("RestaurantDealsError", "Error searching restaurants", e)
            Result.Error(e)
        }
    }

    private fun applyVoteToDeal(deal: RawDeal, voteType: VoteType): RawDeal {
        val previousVote = deal.userVote

        if (previousVote == voteType) {
            return deal
        }

        return when (voteType) {
            VoteType.UPVOTE -> deal.copy(
                userVote = voteType,
                numUpvote = deal.numUpvote + 1,
                numDownvote = deal.numDownvote - (if (previousVote == VoteType.DOWNVOTE) 1 else 0)
            )
            VoteType.DOWNVOTE -> deal.copy(
                userVote = voteType,
                numUpvote = deal.numUpvote - (if (previousVote == VoteType.UPVOTE) 1 else 0),
                numDownvote = deal.numDownvote + 1
            )
            // otherwise the user is removing their vote
            else -> {
                deal.copy(
                    userVote = VoteType.NEUTRAL,
                    numDownvote = if (previousVote == VoteType.DOWNVOTE) {
                        deal.numDownvote - 1
                    } else {
                        deal.numDownvote
                    },
                    numUpvote = if (previousVote == VoteType.UPVOTE) {
                        deal.numUpvote - 1
                    } else {
                        deal.numUpvote
                    }
                )
            }
        }
    }

    /**
     * This is an optimistic update to the accumulated deals.
     * Force/manually rewrites what we have as local state, so we don't have to wait for the
     * network request to finish before updating the UI.
     * TODO: we should be making a network request to update the vote, and then updating the
     */
    private fun updateAccumulatedDealsWithVote(dealId: String, userVote: VoteType) {
        // optimistically update the vote in the accumulated deals
        val updatedDeals = _accumulatedDeals.value.map { restaurant ->
            restaurant.copy(
                rawDeals = restaurant.rawDeals.map { deal ->
                    if (deal.id == dealId) {
                        applyVoteToDeal(deal, userVote)
                    } else {
                        deal
                    }
                }
            )
        }
        _accumulatedDeals.value = updatedDeals
    }

    private fun updateAccumulatedDealsWithSaveAction(dealId: String, saved: Boolean) {
        // optimistically update the vote in the accumulated deals
        val updatedDeals = _accumulatedDeals.value.map { restaurant ->
            restaurant.copy(
                rawDeals = restaurant.rawDeals.map { deal ->
                    if (deal.id == dealId) {
                        deal.copy(userSaved = saved)
                    } else {
                        deal
                    }
                }
            )
        }
        _accumulatedDeals.value = updatedDeals
    }

    private fun updateAccumulatedDealsWithDeleteAction(dealId: String) {
        val updatedDeals = _accumulatedDeals.value.map { restaurant ->
            restaurant.copy(
                rawDeals = restaurant.rawDeals.filter { deal ->
                    deal.id != dealId
                }
            )
        }
        _accumulatedDeals.value = updatedDeals
    }

    override suspend fun updateVote(
        dealId: String,
        userId: String,
        userVote: VoteType
    ): Result<ApiResponse> {
        try {
            val response = apiService.updateVote(dealId, userId, userVote)

            updateAccumulatedDealsWithVote(dealId, userVote)
            return Result.Success(response)
        } catch (e: Exception) {
            Log.e("RestaurantDealsError", "Error updating vote", e)
            return Result.Error(e)
        }
    }

    override suspend fun saveDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return try {
            val response = apiService.saveDeal(dealId, userId)
            updateAccumulatedDealsWithSaveAction(dealId, saved = true)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("RestaurantDealsError", "Error saving deal", e)
            Result.Error(e)
        }
    }

    override suspend fun unsaveDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return try {
            val response = apiService.unsaveDeal(dealId, userId)
            updateAccumulatedDealsWithSaveAction(dealId, saved = false)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("RestaurantDealsError", "Error unsaving deal", e)
            Result.Error(e)
        }
    }

    override suspend fun getSavedDeals(userId: String): Result<List<RestaurantDealsResponse>> {
        return try {
            val response = apiService.getSavedDeals(userId)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("RestaurantDealsError", "Error fetching saved deals", e)
            Result.Error(e)
        }
    }


    override suspend fun deleteDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return try {
            val response = apiService.deleteDeal(dealId, userId)
            if (response.success) {
                updateAccumulatedDealsWithDeleteAction(dealId)
            } else {
                Log.e("RestaurantDealsError", "Error deleting deal: ${response.message}")
            }
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("RestaurantDealsError", "Error deleting deal", e)
            Result.Error(e)
        }
    }

    override suspend fun getRestaurant(
        placeId: String
    ): Result<GetRestaurantResponse> {
        return try {
            val response = apiService.getRestaurant(placeId)
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("getRestaurant", "Error fetching restaurant $placeId", e)
            Result.Error(e)
        }
    }
}
