package com.example.grub.ui.addDealFlow.selectRestaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment

@Composable
fun SelectRestaurantScreen(
    uiState: SelectRestaurantUiState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    println("Select restaurant: ${uiState.deals}")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
            )
        }
    }
}