package com.example.grub.model

import android.annotation.SuppressLint
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

    override fun toString(): String {
        return when (this) {
            MONDAY -> "Monday"
            TUESDAY -> "Tuesday"
            WEDNESDAY -> "Wednesday"
            THURSDAY -> "Thursday"
            FRIDAY -> "Friday"
            SATURDAY -> "Saturday"
            SUNDAY -> "Sunday"
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
    data object NoRestriction : DayOfWeekAndTimeRestriction(RestrictionType.NONE) {
        override fun toDisplayString(): String = "Available anytime"
        override fun toDisplayDay(): String = "Available any day"
        override fun toDisplayTime(): String = "Available any time"
    }

    @Parcelize
    data class DayOfWeekRestriction(
        val activeDays: List<DayOfWeek>,
    ) : DayOfWeekAndTimeRestriction(RestrictionType.DAY_OF_WEEK) {
        override fun toDisplayString(): String {
            return activeDays.joinToString(", ") {
                it.name.lowercase().replaceFirstChar(Char::uppercase)
            }
        }

        override fun toDisplayDay(): String {
            return activeDays.joinToString(", ") {
                it.name.lowercase().replaceFirstChar(Char::uppercase)
            }
        }

        override fun toDisplayTime(): String = "Available all day"
    }

    @Parcelize
    data class BothDayAndTimeRestriction(
        val activeDaysAndTimes: List<DayWithTimeInterval>,
    ) : DayOfWeekAndTimeRestriction(RestrictionType.BOTH_DAY_AND_TIME) {
        @SuppressLint("DefaultLocale")
        override fun toDisplayString(): String {
            val filteredIntervals = activeDaysAndTimes.filterNot {
                it.timeInterval.startHour == 0 && it.timeInterval.startMinute == 0 &&
                        it.timeInterval.endHour == 0 && it.timeInterval.endMinute == 0
            }

            if (filteredIntervals.isEmpty()) return "No specific time restrictions"

            val dayColumnWidth = activeDaysAndTimes.maxOf { it.dayOfWeek.name.length }

            return filteredIntervals.joinToString("\n") { interval ->
                val day = interval.dayOfWeek.name.lowercase().replaceFirstChar(Char::uppercase)
                val paddedDay = day.padEnd(dayColumnWidth)
                val startTime = String.format(
                    "%02d:%02d",
                    interval.timeInterval.startHour,
                    interval.timeInterval.startMinute
                )
                val endTime = String.format(
                    "%02d:%02d",
                    interval.timeInterval.endHour,
                    interval.timeInterval.endMinute
                )
                "$paddedDay: $startTime - $endTime"
            }
        }
        @SuppressLint("DefaultLocale")
        override fun toDisplayTime(): String {
            val filteredIntervals = activeDaysAndTimes.filterNot {
                it.timeInterval.startHour == 0 && it.timeInterval.startMinute == 0 &&
                        it.timeInterval.endHour == 0 && it.timeInterval.endMinute == 0
            }

            if (filteredIntervals.isEmpty()) return "No specific time restrictions"


            return filteredIntervals.joinToString("\n") { interval ->
                val startTime = String.format(
                    "%02d:%02d",
                    interval.timeInterval.startHour,
                    interval.timeInterval.startMinute
                )
                val endTime = String.format(
                    "%02d:%02d",
                    interval.timeInterval.endHour,
                    interval.timeInterval.endMinute
                )
                "$startTime - $endTime"
            }
        }
        override fun toDisplayDay(): String {
            val filteredIntervals = activeDaysAndTimes.filterNot {
                it.timeInterval.startHour == 0 && it.timeInterval.startMinute == 0 &&
                        it.timeInterval.endHour == 0 && it.timeInterval.endMinute == 0
            }

            if (filteredIntervals.isEmpty()) return "No specific time restrictions"

            return filteredIntervals.joinToString("\n") { interval ->
                val day = interval.dayOfWeek.name.lowercase().replaceFirstChar(Char::uppercase)
                day
            }
        }
    }


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
    abstract fun toDisplayString(): String
    abstract fun toDisplayDay(): String
    abstract fun toDisplayTime(): String
}