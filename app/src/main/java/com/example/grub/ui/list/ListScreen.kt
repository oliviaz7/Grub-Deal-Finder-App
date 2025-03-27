package com.example.grub.ui.list

import CustomFilterDialog
import RestaurantItem
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.ui.searchBar.CustomSearchBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListScreen(
    navController: NavController,
    uiState: ListUiState,
    onFilterSelected: (String) -> Unit,
    onSelectCustomFilter: (String, String) -> Unit,
    onSubmitCustomFilter: () -> Unit,
    onShowFilterDialog: (Boolean) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onFilter: () -> Unit,
    onSortOptionSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 12.dp, start = 20.dp, end = 20.dp, top = 4.dp)
            ) {
                CustomSearchBar(
                    Modifier
                        .background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.small
                        )
                        .weight(0.8f)
                        .height(40.dp),
                    uiState.searchText,
                    onSearchTextChange,
                    onFilter,
                    interactionSource
                )

                Button(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .fillMaxWidth()
                        .weight(0.2f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(8.dp),
                    colors = if(uiState.selectedSort=="") ButtonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = Color.White,
                    ) else
                        ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = Color.White,
                        )
                    ,
                ) {
                    Text("Sort")
                }


            }
        },
        containerColor = Color.White,
        modifier = Modifier
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(top = 48.dp)
                .fillMaxHeight()
                .background(Color.White)
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
                    .verticalScroll(scrollState) // Scrollable content
            ) {
                ListFilterButtons(
                    uiState,
                    Modifier
                        .padding(vertical = 4.dp)
                        .defaultMinSize(minWidth = 48.dp, minHeight = 1.dp),
                    onFilterSelected = onFilterSelected,
                    onSelectCustomFilter = onSelectCustomFilter,
                    onSubmitCustomFilter = onSubmitCustomFilter,
                    onShowFilterDialog = onShowFilterDialog,
                )

                if (uiState.filteredDeals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matching deals found",
                            color = Color.Gray,
                        )
                    }
                } else {
                    // Display the list of filtered restaurant deals
                    uiState.filteredDeals.forEach { restaurant ->
                        RestaurantItem(
                            restaurant = restaurant,
                            navController = navController,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                },
                sheetState = sheetState,
                dragHandle = null,
                containerColor = Color.White,
            ) {
                SortingOptions(
                    onSortOptionSelected = { option ->
                        onSortOptionSelected(option)
                        scope.launch {
                            delay(200)
                            showBottomSheet = false
                        }
                    },
                    onCancel = { showBottomSheet = false },
                    selectedSort = uiState.selectedSort,
                )
            }
        }
    }
}

@Composable
fun SortingOptions(
    onSortOptionSelected: (String) -> Unit,
    onCancel: () -> Unit,
    selectedSort: String,
) {
    val sortOptions = listOf("Distance", "Date Posted", "Up Votes")
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp)
    ) {
        Text(
            text = "Sort By",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        sortOptions.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onSortOptionSelected(option) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedSort == option) MaterialTheme.colorScheme.primary
                    else Color.Black,
                    modifier = Modifier.weight(1f),
                )
                if (selectedSort == option) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onCancel() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListFilterButtons(
    uiState: ListUiState,
    modifier: Modifier,
    onFilterSelected: (String) -> Unit,
    onSelectCustomFilter: (String, String) -> Unit,
    onSubmitCustomFilter: () -> Unit,
    onShowFilterDialog: (Boolean) -> Unit,
) {
    val selectedFilter = uiState.selectedFilter
    val showFilterDialog = uiState.showFilterDialog
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween // Space buttons evenly
    ) {
        // All button
        FilterButton(
            selectedFilter,
            "All",
            onFilterSelected,
            modifier
        )
        FilterButton(
            selectedFilter,
            "BOGO",
            onFilterSelected,
            modifier
        )
        FilterButton(
            selectedFilter,
            "Discount",
            onFilterSelected,
            modifier
        )
        FilterButton(
            selectedFilter,
            "Free",
            onFilterSelected,
            modifier
        )
        Button(
            onClick = { onShowFilterDialog(true) },
            colors = if (selectedFilter == "Custom") ButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = Color.White,
            ) else ButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledContentColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = modifier.drawBehind {
                drawRoundRect(
                    color = if (selectedFilter == "Custom")
                        Color.Black.copy(alpha = 0.4f)
                    else
                        Color.Black.copy(alpha = 0.1f),
                    size = size.copy(
                        height = size.height - 12.dp.toPx(),
                        width = size.width,
                    ),
                    topLeft = Offset(8f, 24f),
                    cornerRadius = CornerRadius(x = 24f, y = 24f)
                )
            },
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter icon",
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(16.dp)
                )
                Text(text = "Filter")
            }
        }
        if (showFilterDialog)
            CustomFilterDialog(
                uiState.selectedCustomFilter,
                onSelectCustomFilter = { type: String, filter: String ->
                    onSelectCustomFilter(
                        type,
                        filter
                    )
                },
                onSubmitCustomFilter = onSubmitCustomFilter,
                onShowFilterDialog = onShowFilterDialog
            )
    }
}

@Composable
fun FilterButton(
    selectedFilter: String,
    label: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = { onFilterSelected(label) },

        colors = if (selectedFilter == label) ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = Color.White,
        ) else ButtonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier.drawBehind {
            drawRoundRect(
                color = if (selectedFilter == label)
                    Color.Black.copy(alpha = 0.4f)
                else
                    Color.Black.copy(alpha = 0.1f),
                size = size.copy(
                    height = size.height - 12.dp.toPx(),
                    width = size.width,
                ),
                topLeft = Offset(8f, 24f),
                cornerRadius = CornerRadius(x = 24f, y = 24f)
            )
        },
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(8.dp),
    ) {
        Text(text = label)
    }

}



