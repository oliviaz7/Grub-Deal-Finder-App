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

    ProfileScreen(
        uiState = uiState,
        onClickFavDeals = profileViewModel::onClickFavDeals,
        setShowBottomSheet = profileViewModel::setShowBottomSheet,
        navController = navController,
        onSignOut = profileViewModel::onSignOut,
    )
}
