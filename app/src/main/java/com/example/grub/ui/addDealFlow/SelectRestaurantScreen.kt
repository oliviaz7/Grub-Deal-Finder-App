package com.example.grub.ui.addDealFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng

@Composable
fun SelectRestaurantScreen (
    navController: NavController,
    onNextClick: () -> Unit,
    restaurantName: String,
    onRestaurantNameChange: (String) -> Unit,
    placeId: String,
    onPlaceIdChange: (String) -> Unit,
    coordinates: LatLng,
    onCoordinatesChange: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    fun isNextButtonEnabled(): Boolean {
        return restaurantName.isNotEmpty() && placeId.isNotEmpty()
    }

    // Search Bar (TextField)
    OutlinedTextField(
        value = restaurantName,
        onValueChange = onRestaurantNameChange,
        label = { Text("Search Deals") },
        placeholder = { Text("Type to search") },
        trailingIcon = {
            IconButton(onClick = { /*search nearby places with restaurantName */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )

    Button(
//        enabled = isNextButtonEnabled(),
        onClick = { onNextClick() },
        modifier = modifier
    ) {
        Text("Next")
    }

}


