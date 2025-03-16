package com.example.grub.model

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

// rawIndex is the index it corresponds to in the list that the backend gives us
// [0, 1, 2, 3, 4, 5, 6] -> [Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday]
enum class DayOfWeek(val rawIndex: Int) {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6);

    companion object {
        private val map = entries.associateBy(DayOfWeek::rawIndex)
        fun fromInt(type: Int) = map[type]

        @RequiresApi(Build.VERSION_CODES.O)
        fun fromDateTime(dateTime: ZonedDateTime) = when (dateTime.dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> MONDAY
            java.time.DayOfWeek.TUESDAY -> TUESDAY
            java.time.DayOfWeek.WEDNESDAY -> WEDNESDAY
            java.time.DayOfWeek.THURSDAY -> THURSDAY
            java.time.DayOfWeek.FRIDAY -> FRIDAY
            java.time.DayOfWeek.SATURDAY -> SATURDAY
            java.time.DayOfWeek.SUNDAY -> SUNDAY
            else -> MONDAY // error lol, if we see monday, be alarmed
        }
    }
}

@Parcelize
data class TimeInterval(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
) : Parcelable

@Parcelize
data class DayWithTimeInterval(
    val dayOfWeek: DayOfWeek,
    val timeInterval: TimeInterval,
) : Parcelable {

    @RequiresApi(Build.VERSION_CODES.O)
    fun isAvailableAt(dateTime: ZonedDateTime): Boolean {
        val minutesInDay = dateTime.hour * 60 + dateTime.minute
        val dayOfWeek = DayOfWeek.fromDateTime(dateTime)

        val intervalStart = timeInterval.startHour * 60 + timeInterval.startMinute
        val intervalEnd = timeInterval.endHour * 60 + timeInterval.endMinute
        return this.dayOfWeek == dayOfWeek &&
                minutesInDay >= intervalStart &&
                minutesInDay <= intervalEnd
    }
}

enum class RestrictionType {
    /** Deal is active whenever the restaurant is open. */
    NONE,

    /** Deal is restricted to specific days of the week (e.g., weekday only). */
    DAY_OF_WEEK,

    /** Deal is restricted to specific days and times (e.g., happy hour on weekdays). */
    BOTH_DAY_AND_TIME,
}

@Parcelize
sealed class DayOfWeekAndTimeRestriction(
    val type: RestrictionType,
) : Parcelable {

    @Parcelize
    data object NoRestriction : DayOfWeekAndTimeRestriction(RestrictionType.NONE)

    @Parcelize
    data class DayOfWeekRestriction(
        val activeDays: List<DayOfWeek>,
    ) : DayOfWeekAndTimeRestriction(RestrictionType.DAY_OF_WEEK)

    @Parcelize
    data class BothDayAndTimeRestriction(
        val activeDaysAndTimes: List<DayWithTimeInterval>,
    ) : DayOfWeekAndTimeRestriction(RestrictionType.BOTH_DAY_AND_TIME)

    /**
     * Checks if the deal is available at the given [dateTime].
     * @return True if no restrictions apply or the current day/time matches.
     */
    // TODO: TEST THIS LOL
    @RequiresApi(Build.VERSION_CODES.O)
    fun isAvailableAt(dateTime: ZonedDateTime): Boolean {
        val dayOfWeek = DayOfWeek.fromDateTime(dateTime)

        return when (this) {
            is NoRestriction -> true
            is DayOfWeekRestriction -> activeDays.contains(dayOfWeek)
            is BothDayAndTimeRestriction -> activeDaysAndTimes.any { it.isAvailableAt(dateTime) }
        }
    }
}