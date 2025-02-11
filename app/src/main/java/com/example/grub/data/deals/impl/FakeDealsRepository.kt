package com.example.grub.data.deals.impl

import com.example.grub.data.Result
import com.example.grub.data.deals.DealsRepository
import com.example.grub.data.deals.RawDeal
import com.example.grub.model.DealType
import com.google.android.gms.maps.model.LatLng

class FakeDealsRepository : DealsRepository {

    private val fakeDeals by lazy {
        listOf(
            RawDeal(
                id = "123",
                item = "Fries",
                description = "meow",
                type = DealType.BOGO,
                placeId = "i dont know how this works yet",
                restaurantName = "MCD",
                userId = "beetroot",
                restrictions = "Students only",
                coordinates = LatLng(1.35, 103.87),
                expiryDate = null,
                datePosted = 0L,
            ),
            RawDeal(
                id = "456",
                item = "Milkshake",
                description = "meow",
                type = DealType.BOGO,
                placeId = "shawarma this week ?? <3",
                restaurantName = "Chef Signature",
                userId = "beetroot",
                restrictions = "Students only",
                coordinates = LatLng(1.37, 103.88),
                expiryDate = null,
                datePosted = 0L,
            ),
            RawDeal(
                id = "789",
                item = "Burger",
                description = "meow",
                type = DealType.BOGO,
                placeId = "lolz",
                restaurantName = "Mozy's Shawarma",
                userId = "beetroot",
                restrictions = "Students only",
                coordinates = LatLng(1.35, 103.82),
                expiryDate = null,
                datePosted = 0L,
            )
        )
    }

    override suspend fun getDeals(coordinates: LatLng?, radius: Double): Result<List<RawDeal>> {
        return Result.Success(fakeDeals)
    }
}