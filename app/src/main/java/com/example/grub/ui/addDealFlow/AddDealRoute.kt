package com.example.grub.ui.addDealFlow

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.ui.addDealFlow.screens.AddDetailsScreen
import com.example.grub.ui.addDealFlow.screens.AddImagesScreen
import com.example.grub.ui.addDealFlow.screens.SelectRestaurantScreen
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
        onSearchTextChange = addDealViewModel::onSearchTextChange,
        updateImageUri = addDealViewModel::updateImageUri,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddDealRoute(
    uiState: AddDealUiState,
    navController: NavController,
    uploadTest: (imageUri: Uri) -> Unit,
    addNewRestaurantDeal: (RestaurantDealsResponse) -> Unit,
    searchNearbyRestaurants: (String, LatLng, Double) -> Unit,
    updateRestaurant: (SimpleRestaurant) -> Unit,
    prevStep: () -> Unit,
    nextStep: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    updateImageUri: (Uri?) -> Unit,
) {
    when (uiState.step) {
        Step.Step1 -> {
            SelectRestaurantScreen(
                uiState = uiState,
                navController = navController,
                searchNearbyRestaurants = searchNearbyRestaurants,
                updateRestaurant = updateRestaurant,
                nextStep = nextStep,
                onSearchTextChange = onSearchTextChange,
            )
        }

        Step.Step2 -> {
            AddImagesScreen(
                uiState = uiState,
                navController = navController,
                uploadImage = uploadTest,
                updateImageUri = updateImageUri,
                prevStep = prevStep,
                nextStep = nextStep,
            )
        }

        Step.Step3 -> {
            AddDetailsScreen(
                uiState = uiState,
                navController = navController,
                addNewRestaurantDeal = addNewRestaurantDeal,
                prevStep = prevStep,
            )
        }
    }
}

