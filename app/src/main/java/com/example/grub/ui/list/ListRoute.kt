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
        onSelectCustomFilter = { type: String, filter: String ->
            listViewModel.onSelectCustomFilter(
                type,
                filter
            )
        },
        onSubmitCustomFilter = { -> listViewModel.onSubmitCustomFilter() },
        onShowFilterDialog = { bool: Boolean -> listViewModel.onShowFilterDialog(bool) },
        onFilterSelected = { filter: String -> listViewModel.onFilterSelected(filter) },
        onSearchTextChange = { searchText: String -> listViewModel.onSearchTextChange(searchText) },
        onFilter = { -> listViewModel.onFilter() },
        onSortOptionSelected = { option: String -> listViewModel.onSortOptionSelected(option) }
    )
}
