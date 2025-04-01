package com.example.grub.ui.addDealFlow

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.grub.model.ApplicableGroup
import com.example.grub.model.DealType
import com.example.grub.model.RestaurantDeal
import com.example.grub.ui.addDealFlow.screens.AddDetailsScreen
import com.example.grub.ui.addDealFlow.screens.AddExtraDetailsScreen
import com.example.grub.ui.addDealFlow.screens.AddImagesScreen
import com.example.grub.ui.addDealFlow.screens.SelectRestaurantScreen
import com.example.grub.ui.addDealFlow.screens.ShowExistingDeals
import java.time.ZonedDateTime

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
        uploadImageToFirebase = addDealViewModel::uploadImageToFirebase,
        addNewRestaurantDeal= addDealViewModel::addNewRestaurantDeal,
        searchNearbyRestaurants = addDealViewModel::searchNearbyRestaurants,
        updateRestaurant = addDealViewModel::updateRestaurant,
        nextStep = addDealViewModel::nextStep,
        prevStep = addDealViewModel::prevStep,
        onSearchTextChange = addDealViewModel::onSearchTextChange,
        updateAndroidImageUri = addDealViewModel::updateImageUri,
        updateItemName = addDealViewModel::updateItemName,
        updateDescription = addDealViewModel::updateDescription,
        updatePrice = addDealViewModel::updatePrice,
        updateDealType = addDealViewModel::updateDealType,
        updateExpiryDate = addDealViewModel::updateExpiryDate,
        updateStartTimes = addDealViewModel::updateStartTimes,
        updateEndTimes = addDealViewModel::updateEndTimes,
        updateApplicableGroups = addDealViewModel::updateApplicableGroup,
        onPermissionsChanged = addDealViewModel::onCameraPermissionsChanged,
        getRestaurantDeals = addDealViewModel::getRestaurantDeals,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddDealRoute(
    uiState: AddDealUiState,
    navController: NavController,
    uploadImageToFirebase: (imageUri: Uri) -> Unit,
    addNewRestaurantDeal: () -> Unit,
    searchNearbyRestaurants: (String, Double) -> Unit,
    updateRestaurant: (RestaurantDeal) -> Unit,
    prevStep: (Step?) -> Unit,
    nextStep: (Step?) -> Unit,
    onSearchTextChange: (String) -> Unit,
    updateAndroidImageUri: (Uri?) -> Unit,
    updateItemName: (String) -> Unit,
    updateDescription: (String?) -> Unit,
    updatePrice: (String?) -> Unit,
    updateDealType: (DealType) -> Unit,
    updateExpiryDate: (ZonedDateTime) -> Unit,
    updateStartTimes: (List<Int>) -> Unit,
    updateEndTimes: (List<Int>) -> Unit,
    updateApplicableGroups: (ApplicableGroup) -> Unit,
    onPermissionsChanged: (Boolean) -> Unit,
    getRestaurantDeals: () -> Unit,
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
            ShowExistingDeals(
                uiState = uiState,
                navController = navController,
                getRestaurantDeals = getRestaurantDeals,
                nextStep = nextStep,
                prevStep = prevStep,
            )
        }

        Step.Step3 -> {
            AddImagesScreen(
                uiState = uiState,
                navController = navController,
                uploadImageToFirebase = uploadImageToFirebase,
                updateAndroidImageUri = updateAndroidImageUri,
                onPermissionsChanged = onPermissionsChanged,
                prevStep = prevStep,
                nextStep = nextStep,
            )
        }

        Step.Step4 -> {
            AddDetailsScreen(
                uiState = uiState,
                navController = navController,
                prevStep = prevStep,
                nextStep = nextStep,
                updateItemName = updateItemName,
                updateDescription = updateDescription,
                updatePrice = updatePrice,
                updateDealType = updateDealType,
            )
        }

        Step.Step5 -> {
            AddExtraDetailsScreen(
                uiState = uiState,
                navController = navController,
                addNewRestaurantDeal = addNewRestaurantDeal,
                prevStep = prevStep,
                updateStartTimes = updateStartTimes,
                updateEndTimes = updateEndTimes,
                updateExpiryDate = updateExpiryDate,
                updateApplicableGroups = updateApplicableGroups,
            )
        }
    }
}

