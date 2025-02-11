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
) : Parcelable

enum class DealType {
    BOGO,
    DISCOUNT,
    FREE,
    // TODO: add more
}
