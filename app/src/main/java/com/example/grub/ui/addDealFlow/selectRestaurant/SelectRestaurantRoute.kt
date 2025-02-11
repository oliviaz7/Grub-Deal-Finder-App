package com.example.grub.ui.addDealFlow.selectRestaurant

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grub.ui.map.MapUiState
import androidx.navigation.NavController

/**
 * Displays the SelectRestaurant route.
 *
 * @param selectRestaurantViewModel ViewModel that handles the business logic of this screen
 * @param snackbarHostState (state) state for the [Scaffold] component on this screen
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectRestaurantRoute(
    selectRestaurantViewModel: SelectRestaurantViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() } ,
    navController: NavController,
) {
    // UiState of the HomeScreen
    val uiState by selectRestaurantViewModel.uiState.collectAsStateWithLifecycle()

    SelectRestaurantRoute(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        navController = navController,
    )
}

@Composable
fun SelectRestaurantRoute(
    uiState: SelectRestaurantUiState,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
) {
    SelectRestaurantScreen(uiState, navController)
}

