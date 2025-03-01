package com.example.grub.ui.addDealFlow

import RestaurantItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.data.deals.Restaurant
import com.example.grub.model.RestaurantDeal
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRestaurantScreen (
    uiState: AddDealUiState,
    navController: NavController,
    searchNearbyRestaurants: (String, LatLng, Double) -> Unit,
    updateRestaurant: (Restaurant) -> Unit,
    nextStep : () -> Unit,
    modifier: Modifier = Modifier
) {
    fun isNextButtonEnabled(): Boolean {
        return uiState.restaurant.restaurantName.isNotEmpty() && uiState.restaurant.placeId.isNotEmpty()
    }

    var keyword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Select a Restaurant") },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .background(Color.White)
                .padding(horizontal = 20.dp)
        ) {
            // Search Bar (TextField)
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("Search Deals") },
                placeholder = { Text("Type to search") },
                trailingIcon = {
                    IconButton(onClick = {
                        searchNearbyRestaurants(
                            keyword,
                            uiState.coordinates,
                            1000.0
                        )
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            uiState.restaurants.forEach { restaurant ->
                RestaurantItem(
                    restaurant = restaurant,
                    navController = navController,
                    modifier = Modifier
                        .clickable(onClick = {
                            updateRestaurant(
                                Restaurant(
                                    restaurant.placeId,
                                    restaurant.coordinates,
                                    restaurant.restaurantName
                                )
                            )
                        })
                )
            }

            Button(
                enabled = isNextButtonEnabled(),
                onClick = { nextStep() },
                modifier = modifier
            ) {
                Text("Next")
            }
        }
    }



}




