package com.example.grub.ui.addDealFlow.selectRestaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SelectRestaurantScreen(uiState: SelectRestaurantUiState, modifier: Modifier = Modifier) {
    println("Select restaurant: ${uiState.deals}")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
    ){}
}