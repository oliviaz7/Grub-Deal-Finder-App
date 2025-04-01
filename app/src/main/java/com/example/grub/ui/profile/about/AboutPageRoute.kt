package com.example.grub.ui.profile.about

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AboutPageRoute(
    AboutPageViewModel: AboutPageViewModel,
    navController: NavController,
) {
    val uiState by AboutPageViewModel.uiState.collectAsStateWithLifecycle()

    AboutPageRoute(
        uiState = uiState,
        navController = navController,
        // **add view model functions here**
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AboutPageRoute(
    uiState: AboutPageUiState,
    navController: NavController,
) {
    AboutPageScreen(
        uiState = uiState,
        navController = navController,
    )
}