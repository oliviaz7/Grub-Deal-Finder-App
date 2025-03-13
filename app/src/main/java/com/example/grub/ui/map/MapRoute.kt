package com.example.grub.ui.map

import MapScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/**
 * Displays the Map route.
 *
 * @param mapViewModel ViewModel that handles the business logic of this screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapRoute(
    mapViewModel: MapViewModel,
    navController: NavController, // we added a navController here
) {
    // UiState of the HomeScreen
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    MapRoute(
        uiState = uiState,
        navController = navController,
        onPermissionsChanged = { mapViewModel.onPermissionsChanged(it) },
        onEvent = { event: MapEvent -> mapViewModel.onMapEvent(event) },
    )
}

/**
 * Displays the Home route.
 *
 * This composable is not coupled to any specific state management.
 *
 * @param uiState (state) the data to show on the screen
 * @param snackbarHostState (state) state for the [Scaffold] component on this screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapRoute(
    uiState: MapUiState,
    navController: NavController,
    onPermissionsChanged: (Boolean) -> Unit,
    onEvent: (MapEvent) -> Unit,
) {
    MapScreen(
        uiState = uiState,
        navController = navController,
        onPermissionsChanged = onPermissionsChanged,
        onEvent = onEvent,
    )
}
