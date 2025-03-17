package com.example.grub.ui.profile


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
    navController: NavController,
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    ProfileRoute(
        uiState = uiState,
        onClickFavDeals = { -> profileViewModel.onClickFavDeals() },
        setShowBottomSheet = { show: Boolean -> profileViewModel.setShowBottomSheet(show) },
        navController = navController,
        // **add view model functions here**
    )
}

/**
 * Displays the Profile route.
 *
 * This composable is not coupled to any specific state management.
 *
 * @param uiState (state) the data to show on the screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileRoute(
    uiState: ProfileUiState,
    onClickFavDeals: () -> Unit,
    setShowBottomSheet: (Boolean) -> Unit,
    navController: NavController,
) {
    ProfileScreen(
        uiState = uiState,
        onClickFavDeals = onClickFavDeals,
        setShowBottomSheet = setShowBottomSheet,
        navController = navController,
    )
}
