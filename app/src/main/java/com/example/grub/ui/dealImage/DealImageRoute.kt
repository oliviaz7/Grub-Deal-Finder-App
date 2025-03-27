package com.example.grub.ui.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.grub.ui.dealImage.DealImageScreen
import com.example.grub.ui.dealImage.DealImageViewModel

/**
 * Displays the Deal Image route.
 *
 * @param dealImageViewModel ViewModel that handles the business logic of this screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DealImageRoute(
    dealImageViewModel: DealImageViewModel,
    navController: NavController
) {
    val uiState by dealImageViewModel.uiState.collectAsState()
    DealImageScreen(
        navController,
        uiState,
    )
}
