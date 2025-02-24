package com.example.grub.ui.addDealFlow

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.grub.data.deals.RestaurantDealsResponse

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
    )
}

@Composable
fun AddDealRoute(
    uiState: AddDealUiState,
    navController: NavController,
    uploadTest: (imageUri: Uri) -> Unit,
    addNewRestaurantDeal: (RestaurantDealsResponse) -> Unit,
) {
    AddDealScreen(uiState, navController, uploadTest, addNewRestaurantDeal)
}

