package com.example.grub.ui.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

/**
 * Displays the List route.
 *
 * @param listViewModel ViewModel that handles the business logic of this screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListRoute(
    listViewModel: ListViewModel,
    navController: NavController
) {
    val uiState by listViewModel.uiState.collectAsState()
    ListScreen(
        navController,
        uiState,
        onSelectCustomFilter = listViewModel::onSelectCustomFilter,
        onSubmitCustomFilter = listViewModel::onSubmitCustomFilter,
        onShowFilterDialog = listViewModel::onShowFilterDialog,
        onFilterSelected = listViewModel::onFilterSelected,
        onSearchTextChange = listViewModel::onSearchTextChange,
        onToggleAvailableNow = listViewModel::onToggleAvailableNow,
        onFilter = listViewModel::onFilter,
        onSortOptionSelected = listViewModel::onSortOptionSelected,
        onSelectPriceRange = listViewModel::onSelectPriceRange,
        onClearOptions= listViewModel::onClearOptions,
    )
}
