package com.example.grub.ui.list

import CustomFilterDialog
import RestaurantItem
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    uiState: ListUiState,
    onFilterSelected: (String) -> Unit,
    onSelectCustomFilter: (String, String) -> Unit,
    onSubmitCustomFilter: () -> Unit,
    onShowFilterDialog: (Boolean) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onFilter: () -> Unit
) {
    val scrollState = rememberScrollState()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(uiState.searchText) {
        delay(500)
        onFilter()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 12.dp, start = 20.dp, end = 20.dp, top = 4.dp)
            ) {
                BasicTextField(
                    value = uiState.searchText,
                    onValueChange = onSearchTextChange,
                    textStyle = LocalTextStyle.current,
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.small
                        )
                        .size(300.dp, 40.dp),
                    interactionSource = interactionSource,
                    enabled = true,
                    singleLine = true,
                ) { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(start = 4.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Text Field with Placeholder
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (uiState.searchText.isEmpty()) {
                                Text(
                                    text = "Search",
                                    color = Color.Gray,
                                )
                            }
                            innerTextField()
                        }
                    }

                }

                //not sure what i need to do for sorting???
                //what sort i need, etc
                Button(
                    onClick = {},
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .fillMaxWidth()
                        .size(width = 60.dp, height = 40.dp),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = Color.White,
                    )
                ) {
                    Text("Sort")
                }
            }


        },
        containerColor = Color.White,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
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

                uiState.filteredDeals.forEach { restaurant ->
                    RestaurantItem(
                        restaurant = restaurant,
                        navController = navController,
                    )
                }
            }
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
            .fillMaxWidth(),
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



