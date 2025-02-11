package com.example.grub.model.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.grub.data.deals.RestaurantDealResponse
import com.example.grub.model.Deal
import com.example.grub.model.RestaurantDeal
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object RestaurantDealMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapResponseToRestaurantDeals(response: RestaurantDealResponse): RestaurantDeal {
        val zoneId = ZoneId.of("EST") // hardcoded time zone for now

        val deals = response.rawDeal.map { rawDeal ->
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
                restrictions = rawDeal.restrictions
            )
        }

        return RestaurantDeal(
            id = response.id,
            placeId = response.placeId,
            restaurantName = response.restaurantName,
            coordinates = response.coordinates,
            deals = deals
        )
    }
}