package com.example.grub.ui.profile


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    ProfileRoute(
        uiState = uiState,
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
@Composable
fun ProfileRoute(
    uiState: ProfileUiState,
) {
    ProfileScreen(
        uiState = uiState,
    )
}
