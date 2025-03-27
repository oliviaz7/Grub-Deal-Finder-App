package com.example.grub.model.mappers

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.model.DayOfWeek
import com.example.grub.model.DayOfWeekAndTimeRestriction
import com.example.grub.model.DayWithTimeInterval
import com.example.grub.model.Deal
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.TimeInterval
import com.example.grub.model.VoteType
import com.example.grub.utils.ImageUrlHelper
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

const val MIN_MINUTES_IN_DAY = 0
const val MAX_MINUTES_IN_DAY = 24 * 60

object RestaurantDealMapper {

    /**
     * Maps two lists for start/end times (minutes since midnight) for each day of the week
     * (Mon = 0, Tue = 1, ..., Sun = 6) to a `DayOfWeekAndTimeRestriction`.
     * @param startTimes Start times per day (0-1439); null = no restrictions.
     * @param endTimes End times per day (0-1439); null = no restrictions.
     * If either is null, or one of them is not size 7, take that as no restrictions.
     */
    private fun mapStartEndTimesToRestriction(
        startTimes: List<Int>?,
        endTimes: List<Int>?
    ): DayOfWeekAndTimeRestriction {
        // if either is null, means there are no restrictions
        if (startTimes == null || endTimes == null) {
            return DayOfWeekAndTimeRestriction.NoRestriction
        }

        // error checking, should have 7 days in both lists
        if (startTimes.size != 7 || endTimes.size != 7) {
            Log.e("RestaurantDealMapper", "Invalid startTimes or endTimes")
            return DayOfWeekAndTimeRestriction.NoRestriction
        }
        // If all 7 days have time restrictions but cover the full day (00:00 - 23:59),
        // treat it as NoRestriction
        val isFullDayRestriction =
            startTimes.all { it == 0 } && endTimes.all { it == MAX_MINUTES_IN_DAY }
        if (isFullDayRestriction) {
            return DayOfWeekAndTimeRestriction.NoRestriction
        }

        val hasTimeRestrictions =
            startTimes.any { it != 0 } || endTimes.any { it != MAX_MINUTES_IN_DAY && it != 0 }

        // if all times are either (0,0) or (0,1439), this means there are no specific
        // time restrictions, only day of week restrictions
        if (!hasTimeRestrictions) {
            val activeDays = startTimes.mapIndexedNotNull { index, startTime ->
                val endTime = endTimes[index]
                if (startTime == 0 && endTime == MAX_MINUTES_IN_DAY) {
                    DayOfWeek.fromInt(index)
                } else {
                    null
                }
            }
            return DayOfWeekAndTimeRestriction.DayOfWeekRestriction(activeDays)
        }

        // if there are time restrictions, map them to DayWithTimeInterval
        val dayWithTimeInterval = startTimes.mapIndexedNotNull { index, startTime ->
            val endTime = endTimes[index]
            DayOfWeek.fromInt(index)?.let {
                DayWithTimeInterval(
                    dayOfWeek = it,
                    timeInterval = TimeInterval(
                        startHour = startTime / 60,
                        startMinute = startTime % 60,
                        endHour = endTime / 60,
                        endMinute = endTime % 60,
                    )
                )
            }
        }

        return DayOfWeekAndTimeRestriction.BothDayAndTimeRestriction(dayWithTimeInterval)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapRawDealToDeal(rawDeal: RawDeal): Deal {
        val zoneId = ZoneId.of("EST") // hardcoded time zone for now

        return Deal(
            id = rawDeal.id,
            item = rawDeal.item,
            description = rawDeal.description,
            type = rawDeal.type,
            expiryDate = rawDeal.expiryDate?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zoneId)
            },
            datePosted = ZonedDateTime.ofInstant(Instant.ofEpochMilli(rawDeal.datePosted), zoneId),
            userId = rawDeal.userId,
            imageUrl = ImageUrlHelper.getFullUrl(rawDeal.imageId),
            userSaved = rawDeal.userSaved,
            userVote = rawDeal.userVote ?: VoteType.NEUTRAL,
            applicableGroup = rawDeal.applicableGroup,
            activeDayTime = mapStartEndTimesToRestriction(
                rawDeal.dailyStartTimes,
                rawDeal.dailyEndTimes
            ),
            numUpVotes = rawDeal.numUpvote,
            numDownVotes = rawDeal.numDownvote,
            userName = rawDeal.username ?: "",
            price = rawDeal.price,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapResponseToRestaurantDeals(response: RestaurantDealsResponse): RestaurantDeal {
        val deals = response.rawDeals.map(::mapRawDealToDeal)

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
}
