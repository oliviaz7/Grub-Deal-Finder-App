package com.example.grub.ui.profile.accountDetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.grub.ui.profile.account.AccountDetailsUiState
import com.example.grub.ui.profile.account.AccountDetailsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AccountDetailsRoute(
    AccountDetailsViewModel: AccountDetailsViewModel,
    navController: NavController,
) {
    val uiState by AccountDetailsViewModel.uiState.collectAsStateWithLifecycle()

    AccountDetailsRoute(
        uiState = uiState,
        navController = navController,
        // **add view model functions here**
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AccountDetailsRoute(
    uiState: AccountDetailsUiState,
    navController: NavController,
) {
    AccountDetailsScreen(
        uiState = uiState,
        navController = navController,
    )
}
