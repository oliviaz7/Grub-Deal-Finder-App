package com.example.grub.data.deals.impl

import com.example.grub.data.Result
import com.example.grub.data.deals.ApiResponse
import com.example.grub.data.deals.AddDealResponse
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.model.ApplicableGroup
import com.example.grub.model.DealType
import com.example.grub.model.VoteType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeRestaurantDealsRepository : RestaurantDealsRepository {
    private val _accumulatedDeals = MutableStateFlow<List<RestaurantDealsResponse>>(emptyList())
    override fun accumulatedDeals(): StateFlow<List<RestaurantDealsResponse>> =
        _accumulatedDeals.asStateFlow()

    private val fakeDeals by lazy {
        listOf(
            RestaurantDealsResponse(
                id = "123",
                placeId = "placeId_123",
                coordinates = LatLng(1.35, 103.87),
                restaurantName = "MCD",
                displayAddress = "123 Mommy Park Road, Mississauga",
                rawDeals = listOf(
                    RawDeal(
                        id = "dealId_123",
                        item = "Fries",
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = System.currentTimeMillis(),
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        imageId = "deal_17400.jpg",
                        userSaved = false,
                        userVote = VoteType.NEUTRAL,
                        applicableGroup = ApplicableGroup.STUDENT,
                        // means 4-7PM on Monday - Thursday
                        dailyStartTimes = listOf(840, 840, 840, 840, 0, 0, 0),
                        dailyEndTimes = listOf(1080, 1080, 1080, 1080, 0, 0, 0),
                        numUpvote = 3,
                        numDownvote = 1
                    )
                )
            ),
            RestaurantDealsResponse(
                id = "456",
                placeId = "placeId_456",
                coordinates = LatLng(1.37, 103.88),
                restaurantName = "Chef Signature",
                displayAddress = "123 Mommy Park Road, Mississauga",
                rawDeals = listOf(
                    RawDeal(
                        id = "dealId_456",
                        item = "Milkshake",
                        description = "trying out a longer description to see the ui layout and changes with spacing",
                        type = DealType.BOGO,
                        expiryDate = System.currentTimeMillis(),
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        imageId = "deal_17400123.jpg",
                        userSaved = true,
                        userVote = VoteType.UPVOTE,
                        applicableGroup = ApplicableGroup.STUDENT,
                        // Friday only deal, from 0 - (23 * 60 + 59) minutes
                        dailyStartTimes = listOf(0, 0, 0, 0, 0, 0, 0),
                        dailyEndTimes = listOf(0, 0, 0, 1439, 0, 0, 0),
                        numUpvote = 10,
                        numDownvote = 1
                    ),
                    RawDeal(
                        id = "dealId_456",
                        item = "This one had two",
                        description = "meow",
                        type = DealType.FREE,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "anhela",
                        imageId = "deal_1740033.jpg",
                        userSaved = false,
                        userVote = VoteType.DOWNVOTE,
                        applicableGroup = ApplicableGroup.NEW_USER,
                        // Monday only deal, from 0 - (23 * 60 + 59) minutes
                        dailyStartTimes = listOf(0, 0, 0, 0, 0, 0, 0),
                        dailyEndTimes = listOf(1439, 0, 0, 0, 0, 0, 0),
                        numUpvote = 10,
                        numDownvote = 1,
                    )
                )
            ),
            RestaurantDealsResponse(
                id = "789",
                placeId = "placeId_789",
                coordinates = LatLng(1.35, 103.82),
                displayAddress = "123 Mommy Park Road, Mississauga",
                restaurantName = "Mozy's Shawarma",
                rawDeals = listOf(
                    RawDeal(
                        id = "10796322-ab95-4aea-9a7c-a006cae8eaca",
                        item = "Burger",
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = System.currentTimeMillis(),
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        imageId = null,
                        userSaved = true,
                        userVote = VoteType.UPVOTE,
                        applicableGroup = ApplicableGroup.STUDENT,
                        // means 4-7PM on Monday - Thursday
                        dailyStartTimes = listOf(840, 840, 840, 840, 0, 0, 0),
                        dailyEndTimes = listOf(1080, 1080, 1080, 1080, 0, 0, 0),
                        numUpvote = 10,
                        numDownvote = 1,
                    )
                )
            ),
            // for grace testing
            RestaurantDealsResponse(
                id = "1",
                placeId = "placeId_1",
                coordinates = LatLng(37.4228983, -122.084),
                restaurantName = "Google Top Chicken",
                displayAddress = "123 Lorne Park Road, Mississauga",
                rawDeals = listOf(
                    RawDeal(
                        id = "dealId_123",
                        item = "Fries",
                        description = "available 4-7PM on Monday - Thursday",
                        type = DealType.BOGO,
                        expiryDate = System.currentTimeMillis(),
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        imageId = "deal_17400.jpg",
                        userSaved = true,
                        userVote = VoteType.NEUTRAL,
                        applicableGroup = ApplicableGroup.NONE,
                        // means 4-7PM on Monday - Thursday
                        dailyStartTimes = listOf(840, 840, 840, 840, 0, 0, 0),
                        dailyEndTimes = listOf(1080, 1080, 1080, 1080, 0, 0, 0),
                        numUpvote = 10,
                        numDownvote = 1,
                    )
                )
            ),

            RestaurantDealsResponse(
                id = "2",
                placeId = "placeId_2",
                coordinates = LatLng(37.4210983, -122.084),
                restaurantName = "Google Popeyes",
                displayAddress = "123 Lorne Park Road, Mississauga",
                rawDeals = listOf(
                    RawDeal(
                        id = "dealId_123",
                        item = "Fries",
                        description = "available all day any day",
                        type = DealType.BOGO,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        imageId = "deal_17400.jpg",
                        userSaved = false,
                        userVote = VoteType.NEUTRAL,
                        applicableGroup = ApplicableGroup.BIRTHDAY,
                        dailyStartTimes = null,
                        dailyEndTimes = null,
                        numUpvote = 10,
                        numDownvote = 0,
                    )
                )
            ),

            RestaurantDealsResponse(
                id = "3",
                placeId = "placeId_3",
                coordinates = LatLng(37.4219983, -122.08286),
                restaurantName = "Google Gols",
                displayAddress = "123 Lorne Park Road, Mississauga",
                rawDeals = listOf(
                    RawDeal(
                        id = "dealId_123",
                        item = "Fries",
                        description = "available all day any day",
                        type = DealType.BOGO,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        imageId = "deal_17400.jpg",
                        dailyStartTimes = null,
                        dailyEndTimes = null,
                        numUpvote = 1,
                        numDownvote = 17
                    )
                )
            ),

            )
    }

    private val fakeSimpleRestaurants by lazy {
        listOf(
            SimpleRestaurant(
                placeId = "placeId_123",
                coordinates = LatLng(1.35, 103.87),
                restaurantName = "MCD",
                displayAddress = "123 Philip St",
            ),
            SimpleRestaurant(
                placeId = "placeId_456",
                coordinates = LatLng(1.37, 103.88),
                restaurantName = "Chef Signature",
                displayAddress = "Your moms house"
            ),
            SimpleRestaurant(
                placeId = "placeId_789",
                coordinates = LatLng(1.35, 103.82),
                restaurantName = "Mozy's Shawarma",
                displayAddress = "Missisauga"
            )
        )
    }

    private val fakeDealToAdd by lazy {
        AddDealResponse("10796322-ab95-4aea-9a7c-a006cae8eaca")
    }

    private val fakeSavedDeals by lazy {
        listOf(
            "10796322-ab95-4aea-9a7c-a006cae8eaca",
            "5073a84e-d3a9-4ae7-8194-03bb28ad3c21"
        )
    }

    override suspend fun getRestaurantDeals(coordinates: LatLng, radius: Double): Result<Unit> {
        _accumulatedDeals.update { currentDeals ->
            (currentDeals + fakeDeals).distinctBy { it.id }
        }
        return Result.Success(Unit)
    }

    override suspend fun addRestaurantDeal(deal: RestaurantDealsResponse): Result<AddDealResponse> {
        return Result.Success(fakeDealToAdd)
    }

    override suspend fun searchNearbyRestaurants(
        keyword: String,
        coordinates: LatLng,
        radius: Double
    ): Result<List<SimpleRestaurant>> {
        return Result.Success(fakeSimpleRestaurants)
    }

    override suspend fun updateVote(
        dealId: String,
        userId: String,
        userVote: VoteType
    ): Result<ApiResponse> {
        return Result.Success(ApiResponse(success = true, message = "Vote updated"))
    }

    override suspend fun saveDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return Result.Success(ApiResponse(success = true, message = "Deal saved"))
    }

    override suspend fun unsaveDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return Result.Success(ApiResponse(success = true, message = "Deal unsaved"))
    }

    override suspend fun getSavedDeals(
        userId: String
    ): Result<List<String>>{
        return Result.Success(fakeSavedDeals)
    }

    override suspend fun deleteDeal(
        dealId: String,
        userId: String
    ): Result<ApiResponse> {
        return Result.Success(ApiResponse(success = true, message = "Deal deleted"))
    }
}