package com.example.grub.ui.addDealFlow

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.grub.data.deals.Restaurant
import com.example.grub.data.deals.RestaurantDealsResponse
import com.google.android.gms.maps.model.LatLng

/**
 * Displays the SelectRestaurant route.
 *
 * @param addDealViewModel ViewModel that handles the business logic of this screen
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddDealRoute(
    addDealViewModel: AddDealViewModel,
    navController: NavController,
) {
    // UiState of the HomeScreen
    val uiState by addDealViewModel.uiState.collectAsStateWithLifecycle()

    AddDealRoute(
        uiState = uiState,
        navController = navController,
        uploadTest = addDealViewModel.uploadImage,
        addNewRestaurantDeal= addDealViewModel::addNewRestaurantDeal,
        searchNearbyRestaurants = addDealViewModel::searchNearbyRestaurants,
        updateRestaurant = addDealViewModel::updateRestaurant,
        nextStep = addDealViewModel::nextStep,
        prevStep = addDealViewModel::prevStep,
    )
}

@Composable
fun AddDealRoute(
    uiState: AddDealUiState,
    navController: NavController,
    uploadTest: (imageUri: Uri) -> Unit,
    addNewRestaurantDeal: (RestaurantDealsResponse) -> Unit,
    searchNearbyRestaurants: (String, LatLng, Double) -> Unit,
    updateRestaurant: (Restaurant) -> Unit,
    prevStep: () -> Unit,
    nextStep: () -> Unit,
) {
    if (uiState.step == Step.StepOne) {
        SelectRestaurantScreen(
            uiState = uiState,
            navController = navController,
            searchNearbyRestaurants = searchNearbyRestaurants,
            updateRestaurant = updateRestaurant,
            nextStep = nextStep,
        )
    } else {
        AddDealScreen(
            uiState = uiState,
            navController = navController,
            uploadImage = uploadTest,
            addNewRestaurantDeal = addNewRestaurantDeal,
            searchNearbyRestaurants = searchNearbyRestaurants,
            prevStep = prevStep,
            nextStep = nextStep,
        )
    }
}

