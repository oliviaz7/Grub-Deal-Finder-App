package com.example.grub.ui.map

import android.os.Build
import androidx.annotation.RequiresApi
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
    navController: NavController,
) {
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    MapRoute(
        uiState = uiState,
        navController = navController,
        onPermissionsChanged = { mapViewModel.onPermissionsChanged(it) },
        onEvent = { event: MapEvent -> mapViewModel.onMapEvent(event) },
    )
}

/**
 * Displays the Map route.
 *
 * @param uiState (state) the data to show on the screen
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
        onMapEvent = onEvent,
    )
}
