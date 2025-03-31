package com.example.grub.model.mappers

import com.example.grub.model.DealType

object DealTypeMapper {
    fun mapToDealType(dealType: String): DealType {
        return when (dealType) {
            "BOGO" -> DealType.BOGO
            "DISCOUNT" -> DealType.DISCOUNT
            "FREE" -> DealType.FREE
            else -> DealType.OTHER
        }
    }
}