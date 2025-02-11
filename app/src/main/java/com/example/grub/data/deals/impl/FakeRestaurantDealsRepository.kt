package com.example.grub.data.deals.impl

import com.example.grub.data.Result
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealResponse
import com.example.grub.model.DealType
import com.google.android.gms.maps.model.LatLng

class FakeRestaurantDealsRepository : RestaurantDealsRepository {

    private val fakeDeals by lazy {
        listOf(
            RestaurantDealResponse(
                id = "123",
                placeId = "placeId_123",
                coordinates = LatLng(1.35, 103.87),
                restaurantName = "MCD",
                rawDeal = listOf(
                    RawDeal(
                        id = "dealId_123",
                        item = "Fries",
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        restrictions = "Students only"
                    )
                )
            ),
            RestaurantDealResponse(
                id = "456",
                placeId = "placeId_456",
                coordinates = LatLng(1.37, 103.88),
                restaurantName = "Chef Signature",
                rawDeal = listOf(
                    RawDeal(
                        id = "dealId_456",
                        item = "Milkshake",
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        restrictions = "Students only"
                    ),
                    RawDeal(
                        id = "dealId_456",
                        item = "This one had two",
                        description = "meow",
                        type = DealType.FREE,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "anhela",
                        restrictions = "Students only"
                    )
                )
            ),
            RestaurantDealResponse(
                id = "789",
                placeId = "placeId_789",
                coordinates = LatLng(1.35, 103.82),
                restaurantName = "Mozy's Shawarma",
                rawDeal = listOf(
                    RawDeal(
                        id = "dealId_789",
                        item = "Burger",
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = null,
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        restrictions = "Students only"
                    )
                )
            )
        )
    }

    override suspend fun getRestaurantDeals(coordinates: LatLng?, radius: Double): Result<List<RestaurantDealResponse>> {
        return Result.Success(fakeDeals)
    }
}