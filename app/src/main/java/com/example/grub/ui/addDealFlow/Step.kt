package com.example.grub.ui.addDealFlow

sealed class Step {
    object Step1 : Step() // Select Restaurant
    object Step2 : Step() // Add images
    object Step3 : Step() // Add deal details

    fun nextStep() : Step {
        return when (this) {
            is Step1 -> Step2
            is Step2 -> Step3
            is Step3 -> Step3
        }
    }

    fun prevStep() : Step {
        return when (this) {
            is Step1 -> Step1
            is Step2 -> Step1
            is Step3 -> Step3
        }
    }
}
