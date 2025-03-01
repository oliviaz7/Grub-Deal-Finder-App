package com.example.grub.data.deals.impl

import com.example.grub.data.Result
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.model.DealType
import com.google.android.gms.maps.model.LatLng

class FakeRestaurantDealsRepository : RestaurantDealsRepository {

    private val fakeDeals by lazy {
        listOf(
            RestaurantDealsResponse(
                id = "123",
                placeId = "placeId_123",
                coordinates = LatLng(1.35, 103.87),
                restaurantName = "MCD",
                rawDeals = listOf(
                    RawDeal(
                        id = "dealId_123",
                        item = "Fries",
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        restrictions = "Students only",
                        imageId = "deal_17400.jpg"
                    )
                )
            ),
            RestaurantDealsResponse(
                id = "456",
                placeId = "placeId_456",
                coordinates = LatLng(1.37, 103.88),
                restaurantName = "Chef Signature",
                rawDeals = listOf(
                    RawDeal(
                        id = "dealId_456",
                        item = "Milkshake",
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        restrictions = "Students only",
                        imageId = "deal_17400123.jpg"
                    ),
                    RawDeal(
                        id = "dealId_456",
                        item = "This one had two",
                        description = "meow",
                        type = DealType.FREE,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "anhela",
                        restrictions = "Students only",
                        imageId = "deal_1740033.jpg"
                    )
                )
            ),
            RestaurantDealsResponse(
                id = "789",
                placeId = "placeId_789",
                coordinates = LatLng(1.35, 103.82),
                restaurantName = "Mozy's Shawarma",
                rawDeals = listOf(
                    RawDeal(
                        id = "dealId_789",
                        item = "Burger",
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        restrictions = "Students only",
                        imageId = null,
                    )
                )
            )
        )
    }

    private val fakeSimpleRestaurants by lazy {
        listOf(
            SimpleRestaurant(
                placeId = "placeId_123",
                coordinates = LatLng(1.35, 103.87),
                restaurantName = "MCD"
            ),
            SimpleRestaurant(
                placeId = "placeId_456",
                coordinates = LatLng(1.37, 103.88),
                restaurantName = "Chef Signature"
            ),
            SimpleRestaurant(
                placeId = "placeId_789",
                coordinates = LatLng(1.35, 103.82),
                restaurantName = "Mozy's Shawarma"
            )
        )
    }

    override suspend fun getRestaurantDeals(coordinates: LatLng, radius: Double): Result<List<RestaurantDealsResponse>> {
        return Result.Success(fakeDeals)
    }

    override suspend fun addRestaurantDeal(deal: RestaurantDealsResponse): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun searchNearbyRestaurants(keyword: String, coordinates: LatLng, radius: Double): Result<List<SimpleRestaurant>> {
        return Result.Success(fakeSimpleRestaurants)
    }
}