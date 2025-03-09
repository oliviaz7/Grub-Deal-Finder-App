package com.example.grub.model.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.model.Deal
import com.example.grub.model.RestaurantDeal
import com.example.grub.utils.ImageUrlHelper
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object RestaurantDealMapper {

    @RequiresApi(Build.VERSION_CODES.O)
        fun mapResponseToRestaurantDeals(response: RestaurantDealsResponse): RestaurantDeal {
        val zoneId = ZoneId.of("EST") // hardcoded time zone for now
        val deals = response.rawDeals.map { rawDeal ->
            Deal(
                id = rawDeal.id,
                item = rawDeal.item,
                description = rawDeal.description,
                type = rawDeal.type,
                expiryDate = rawDeal.expiryDate?.let {
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zoneId)
                },
                datePosted = ZonedDateTime.ofInstant(Instant.ofEpochMilli(rawDeal.datePosted), zoneId),
                userId = rawDeal.userId,
                restrictions = rawDeal.restrictions,
                imageUrl = ImageUrlHelper.getFullUrl(rawDeal.imageId),
            )
        }

        return RestaurantDeal(
            id = response.id,
            placeId = response.placeId,
            restaurantName = response.restaurantName,
            coordinates = response.coordinates,
            deals = deals,
            displayAddress = response.displayAddress,
            imageUrl = response.imageUrl,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapResponseToRestaurantDeals(response: SimpleRestaurant): RestaurantDeal {
        val zoneId = ZoneId.of("EST") // hardcoded time zone for now

        return RestaurantDeal(
            id = "",
            placeId = response.placeId,
            restaurantName = response.restaurantName,
            coordinates = response.coordinates,
            deals = emptyList(),
            displayAddress = "", //todo: maybe angela should try to see if this is the intended solution?
            imageUrl = null, //todo: ^^
        )
    }
}