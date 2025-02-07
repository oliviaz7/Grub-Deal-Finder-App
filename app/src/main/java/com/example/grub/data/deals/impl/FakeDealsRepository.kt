package com.example.grub.data.deals.impl

import com.example.grub.data.Result
import com.example.grub.data.deals.DealsRepository
import com.example.grub.model.Deal
import com.example.grub.model.DealType

class FakeDealsRepository : DealsRepository {

    private val fakeDeals by lazy {
        listOf(
            Deal(
                id = "123",
                item = "Fries",
                description = "meow",
                type = DealType.BOGO,
                placeId = "i dont know how this works yet",
                restaurantName = "MCD",
                userId = "beetroot",
                restrictions = "Students only",
            ),
            Deal(
                id = "456",
                item = "Milkshake",
                description = "meow",
                type = DealType.BOGO,
                placeId = "i dont know how this works yet",
                restaurantName = "MCD",
                userId = "beetroot",
                restrictions = "Students only",
            ),
            Deal(
                id = "789",
                item = "Burger",
                description = "meow",
                type = DealType.BOGO,
                placeId = "i dont know how this works yet",
                restaurantName = "MCD",
                userId = "beetroot",
                restrictions = "Students only",
            )
        )
    }

    override suspend fun getDeals(): Result<List<Deal>> {
        return Result.Success(fakeDeals)
    }
}