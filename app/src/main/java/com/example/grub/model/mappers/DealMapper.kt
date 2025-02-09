package com.example.grub.model.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.grub.data.deals.RawDeal
import com.example.grub.model.Deal
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object DealMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapRawDealToDeal(rawDeal: RawDeal): Deal {
        val zoneId = ZoneId.of("EST") // hardcoded time zone for now

        return Deal(
            id = rawDeal.id,
            item = rawDeal.item,
            description = rawDeal.description,
            type = rawDeal.type,
            coordinates = rawDeal.coordinates,
            placeId = rawDeal.placeId,
            restaurantName = rawDeal.restaurantName,
            // double check that unix timestamp to ZonedDateTime works
            expiryDate = rawDeal.expiryDate?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zoneId)
            },
            datePosted = ZonedDateTime.ofInstant(Instant.ofEpochMilli(rawDeal.datePosted), zoneId),
            userId = rawDeal.userId,
            restrictions = rawDeal.restrictions
        )
    }
}