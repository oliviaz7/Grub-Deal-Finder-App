package com.example.grub.ui.addDealFlow.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.model.DealType
import com.example.grub.ui.addDealFlow.AddDealUiState
import java.util.Calendar
import com.example.grub.ui.addDealFlow.components.TimeSelector
import com.example.grub.ui.addDealFlow.components.ConfirmationDialog
import com.example.grub.ui.addDealFlow.components.DollarInputField
import com.example.grub.ui.addDealFlow.components.TitledOutlinedTextField
import com.example.grub.ui.addDealFlow.components.HidableSection
import com.example.grub.ui.addDealFlow.components.SectionDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExtraDetailsScreen(
    uiState: AddDealUiState,
    navController: NavController,
    addNewRestaurantDeal: (RestaurantDealsResponse) -> Unit,
    prevStep: () -> Unit,
    updateStartTimes: (List<Int>) -> Unit,
    updateEndTimes: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    var itemName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedDealType: DealType? by remember { mutableStateOf(null) }
    val dealTypes = DealType.entries.toList()

    // State variables for Date Picker and selected date
    val expiryCalendar = Calendar.getInstance()
    var expirySelectedDate by remember { mutableStateOf("") } // expiry date is empty string or DD/MM/YYYY
    var expiryIsDialogOpen by remember { mutableStateOf(false) }

    // Trigger to show DatePickerDialog
    if (expiryIsDialogOpen) {
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                // Format selected date
                expirySelectedDate = String.format(
                    "%02d/%02d/%04d",
                    dayOfMonth,
                    month + 1,
                    year
                ) // tell Joyce the new date format
                expiryIsDialogOpen = false
            },
            expiryCalendar.get(Calendar.YEAR),
            expiryCalendar.get(Calendar.MONTH),
            expiryCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun isSubmitButtonEnabled(): Boolean {
        return uiState.selectedRestaurant.restaurantName.isNotEmpty()
                && itemName.isNotEmpty()
                && uiState.selectedRestaurant.placeId.isNotEmpty()
                && selectedDealType !== null
    }

    if (showDialog) {
        ConfirmationDialog(
            navController = navController,
            result = uiState.addDealResult,
            onDismiss = { showDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Add Details")
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = prevStep,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isSubmitButtonEnabled(),
                    onClick = {
                        addNewRestaurantDeal(
                            RestaurantDealsResponse(
                                id = "default_id",
                                placeId = uiState.selectedRestaurant.placeId,
                                coordinates = uiState.selectedRestaurant.coordinates,
                                restaurantName = uiState.selectedRestaurant.restaurantName,
                                displayAddress = "restaurant_addy",
                                rawDeals = listOf(
                                    RawDeal( // TODO: add price to raw deal obj
                                        id = "default_deal_id",
                                        item = itemName,
                                        description = description,
                                        type = selectedDealType!!,
                                        expiryDate = getExpiryTimestamp(expirySelectedDate),
                                        datePosted = System.currentTimeMillis(),
                                        userId = "default_user_id",
                                        restrictions = "None",
                                        imageId = uiState.imageUri?.path, // idk if this is right
                                    )
                                )
                            )
                        )
                        showDialog = true
                    },
                ) {
                    Text("Submit")
                }
            }
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .padding(top = 30.dp)
        ) {

            HidableSection(
                content = {
                    TimeSelector(
                        labels = listOf("M", "T", "W", "Th", "F", "S", "Sun"),
                    )
                },
                showContentWhenChecked = false,
                isChecked = true,
                title = "When can you get the deal?",
                label = "Anytime, anyday!",
            )

            SectionDivider(
                modifier = Modifier
            )

            // textfield for description
            TitledOutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                text = null,
                placeholder = "E.g. Whopper deal includes whopper, fries and drink",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
            )

            // TextField for price
            DollarInputField(
                value = price,
                onValueChange = { price = it },
                label = "Price",
                text = null,
                placeholder = "0.00",
            )

            // selecting deal type
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TitledOutlinedTextField(
                    value = selectedDealType?.toString() ?: "",
                    onValueChange = {},
                    label = "Deal Type",
                    text = null,
                    placeholder = "Select a deal type",
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    maxLines = 1,
                    optional = false,
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    dealTypes.forEach { dealType ->
                        DropdownMenuItem(
                            text = { Text(dealType.name) },
                            onClick = {
                                selectedDealType = dealType
                                expanded = false
                            }
                        )
                    }
                }
            }


        }
    }
}

