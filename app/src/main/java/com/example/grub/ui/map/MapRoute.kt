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

    MapScreen(
        uiState = uiState,
        navController = navController,
        onPermissionsChanged = mapViewModel::onPermissionsChanged,
        onMapEvent = mapViewModel::onMapEvent,
    )
}
