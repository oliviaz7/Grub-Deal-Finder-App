package com.example.grub.ui.addDealFlow.selectRestaurant

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/**
 * Displays the SelectRestaurant route.
 *
 * @param selectRestaurantViewModel ViewModel that handles the business logic of this screen
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectRestaurantRoute(
    selectRestaurantViewModel: SelectRestaurantViewModel,
    navController: NavController,
) {
    // UiState of the HomeScreen
    val uiState by selectRestaurantViewModel.uiState.collectAsStateWithLifecycle()

    SelectRestaurantRoute(
        uiState = uiState,
        navController = navController,
        uploadTest = selectRestaurantViewModel.uploadImage,
    )
}

@Composable
fun SelectRestaurantRoute(
    uiState: SelectRestaurantUiState,
    navController: NavController,
    uploadTest: (imageUri: Uri) -> Unit,
) {
    SelectRestaurantScreen(uiState, navController, uploadTest)
}

