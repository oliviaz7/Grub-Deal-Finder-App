package com.example.grub.data.deals

import com.google.gson.annotations.SerializedName

data class AutoPopulateDealsResponse (
    @SerializedName("item_name") val itemName: String?,
    @SerializedName("deal_description") val dealDescription: String?,
    @SerializedName("expiry_date") val expiryDate: String?,
    @SerializedName("price") val price: String?,
    @SerializedName("deal_type") val dealType: String?,
    @SerializedName("applicable_group") val applicableGroup: String?,
)
