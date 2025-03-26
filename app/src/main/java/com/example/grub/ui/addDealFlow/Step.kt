package com.example.grub.ui.addDealFlow

sealed class Step {
    object Step1 : Step() // Select Restaurant
    object Step2 : Step() // show existing deals
    object Step3 : Step() // Add images
    object Step4 : Step() // Add deal details
    object Step5 : Step() // Add more deal details


    fun nextStep() : Step {
        return when (this) {
            is Step1 -> Step2
            is Step2 -> Step3
            is Step3 -> Step4
            is Step4 -> Step5
            is Step5 -> Step5
        }
    }

    fun prevStep() : Step {
        return when (this) {
            is Step1 -> Step1
            is Step2 -> Step1
            is Step3 -> Step2
            is Step4 -> Step3
            is Step5 -> Step4
        }
    }
}
