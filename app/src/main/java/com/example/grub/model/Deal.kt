package com.example.grub.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class Deal(
    val id: String,
    val item: String,
    val description: String? = null,
    val type: DealType,
    val expiryDate: ZonedDateTime? = null,
    val datePosted: ZonedDateTime,
    val userId: String,
    val restrictions: String, // TODO: figure out how we're handling
    val imageUrl: String?,
    val userSaved: Boolean,
    val userVote: VoteType,
    val applicableGroup: ApplicableGroup,
    val activeDayTime: DayOfWeekAndTimeRestriction,
) : Parcelable

enum class DealType {
    BOGO,
    DISCOUNT,
    FREE,
    OTHER;

    // TODO: add more.
    // NOTE: whenever you add a DealType, please add the enum in supabase as well.
    //       Go to Database > DATABASE MANAGEMENT > Enumerated Types > Update type (for DealType) > add value for new enum
}

enum class VoteType(val value: Int) {
    NEUTRAL(0),
    UPVOTE(1),
    DOWNVOTE(-1)
}

enum class ApplicableGroup {
    NONE,
    UNDER_18,
    SENIOR,
    STUDENT,
    LOYALTY_MEMBER,
    NEW_USER,
    BIRTHDAY,

    // TODO: add more.
    // NOTE: whenever you add a ApplicableGroup, please add the enum in supabase as well.
    //       Go to Database > DATABASE MANAGEMENT > Enumerated Types > Update type (for ApplicableGroup) > add value for new enum
}
