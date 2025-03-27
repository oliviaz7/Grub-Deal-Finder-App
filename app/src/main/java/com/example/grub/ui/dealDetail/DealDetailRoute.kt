package com.example.grub.ui.dealDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/**
 * Displays the Deal Detail route.
 *
 * @param dealDetailViewModel ViewModel that handles the business logic of this screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DealDetailRoute(
    dealDetailViewModel: DealDetailViewModel,
    navController: NavController
) {
    val uiState by dealDetailViewModel.uiState.collectAsStateWithLifecycle()

    DealDetailScreen(
        uiState,
        navController,
        onSaveClicked = dealDetailViewModel::onSaveClicked,
        onUpVoteClicked = dealDetailViewModel::onUpVoteClicked,
        onDownVoteClicked = dealDetailViewModel::onDownVoteClicked,
        setShowBottomSheet = dealDetailViewModel::setShowBottomSheet,
    )
}
