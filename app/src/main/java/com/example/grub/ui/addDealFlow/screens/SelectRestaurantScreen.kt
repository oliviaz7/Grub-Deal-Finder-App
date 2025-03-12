package com.example.grub.ui.addDealFlow.screens

import RestaurantItem
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.ui.addDealFlow.AddDealUiState
import com.example.grub.ui.searchBar.CustomSearchBar
import com.google.android.gms.maps.model.LatLng

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRestaurantScreen (
    uiState: AddDealUiState,
    navController: NavController,
    searchNearbyRestaurants: (String, LatLng, Double) -> Unit,
    updateRestaurant: (SimpleRestaurant) -> Unit,
    nextStep : () -> Unit,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
            ) {
                // Search Bar (TextField)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(bottom = 12.dp, start = 20.dp, end = 20.dp, top = 4.dp)
                ) {
                    CustomSearchBar(
                        Modifier
                            .background(
                                color = Color.White,
                                shape = MaterialTheme.shapes.small
                            )
                            .weight(0.8f)
                            .height(40.dp),
                        searchText = uiState.restaurantSearchText,
                        onSearchTextChange = onSearchTextChange,
                        onFilter = {
                            searchNearbyRestaurants(
                                uiState.restaurantSearchText,
                                uiState.coordinates,
                                if (uiState.restaurantSearchText.isEmpty()) 1000.0 else 5000.0
                            )
                        }
                    )
                }

                LazyColumn (modifier = Modifier.padding(horizontal = 20.dp)){
                    items(uiState.restaurants) { restaurant ->
                        RestaurantItem(
                            restaurant = restaurant,
                            navController = navController,
                            modifier = Modifier
                                .clickable(onClick = {
                                    updateRestaurant(
                                        SimpleRestaurant(
                                            restaurant.placeId,
                                            restaurant.coordinates,
                                            restaurant.restaurantName
                                        )
                                    )
                                    nextStep()
                                })
                        )
                    }
                }
            }

    }
}




