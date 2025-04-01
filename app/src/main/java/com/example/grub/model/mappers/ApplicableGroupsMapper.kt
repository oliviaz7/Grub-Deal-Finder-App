package com.example.grub.model.mappers

import com.example.grub.model.ApplicableGroup


object ApplicableGroupsMapper {
    fun mapToApplicableGroups(applicableGroup: String): ApplicableGroup {
        return when (applicableGroup) {
            "UNDER_18" -> ApplicableGroup.UNDER_18
            "STUDENT" -> ApplicableGroup.STUDENT
            "SENIOR" -> ApplicableGroup.SENIOR
            "LOYALTY_MEMBER" -> ApplicableGroup.LOYALTY_MEMBER
            "NEW_USER" -> ApplicableGroup.NEW_USER
            "BIRTHDAY" -> ApplicableGroup.BIRTHDAY
            "EVERYONE" -> ApplicableGroup.ALL
            else -> ApplicableGroup.NONE
        }
    }
}