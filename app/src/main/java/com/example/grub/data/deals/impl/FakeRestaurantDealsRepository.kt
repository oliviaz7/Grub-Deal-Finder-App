package com.example.grub.data.deals.impl

import com.example.grub.data.Result
import com.example.grub.data.deals.AddDealResponse
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
                        restrictions = "Students only",
                        imageId = null,
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
                        description = "meow",
                        type = DealType.BOGO,
                        expiryDate = System.currentTimeMillis(),
                        datePosted = System.currentTimeMillis(),
                        userId = "beetroot",
                        restrictions = "Students only",
                        imageId = "deal_17400.jpg"
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
                id = "3",
                placeId = "placeId_3",
                coordinates = LatLng(37.4219983, -122.08286),
                restaurantName = "Google Gols",
                displayAddress = "123 Lorne Park Road, Mississauga",
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

    private val fakeDealToAdd by lazy {
        AddDealResponse("10796322-ab95-4aea-9a7c-a006cae8eaca")
    }

    override suspend fun getRestaurantDeals(coordinates: LatLng, radius: Double): Result<List<RestaurantDealsResponse>> {
        return Result.Success(fakeDeals)
    }

    override suspend fun addRestaurantDeal(deal: RestaurantDealsResponse): Result<AddDealResponse> {
        return Result.Success(fakeDealToAdd)
    }

    override suspend fun searchNearbyRestaurants(keyword: String, coordinates: LatLng, radius: Double): Result<List<SimpleRestaurant>> {
        return Result.Success(fakeSimpleRestaurants)
    }
}