package com.example.grub.ui.addDealFlow.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
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
import androidx.compose.ui.Alignment
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
import com.example.grub.ui.addDealFlow.components.CustomCheckBox
import com.example.grub.ui.addDealFlow.components.DollarInputField
import com.example.grub.ui.addDealFlow.components.TitledOutlinedTextField
import com.example.grub.ui.addDealFlow.components.HidableSection
import com.example.grub.ui.addDealFlow.components.SectionDivider
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExtraDetailsScreen(
    uiState: AddDealUiState,
    navController: NavController,
    addNewRestaurantDeal: (RestaurantDealsResponse) -> Unit,
    prevStep: () -> Unit,
    updateStartTimes: (List<Int>) -> Unit,
    updateEndTimes: (List<Int>) -> Unit,
    updateExpiryDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()


    // State variables for Date Picker and selected date
    val expiryCalendar = Calendar.getInstance()
    var expiryIsDialogOpen by remember { mutableStateOf(false) }

    fun isSubmitButtonEnabled(): Boolean {
        return uiState.selectedRestaurant.restaurantName.isNotEmpty()
                && uiState.dealState.itemName.isNotEmpty()
                && uiState.selectedRestaurant.placeId.isNotEmpty()
                && uiState.dealState.dealType !== null
    }

    if (showDialog) {
        ConfirmationDialog(
            navController = navController,
            result = uiState.addDealResult,
            onDismiss = { showDialog = false }
        )
    }

    // Trigger to show DatePickerDialog
    if (expiryIsDialogOpen) {
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                // Format selected date
                val expirySelectedDate = String.format(
                    "%02d/%02d/%04d",
                    dayOfMonth,
                    month + 1,
                    year
                ) // tell Joyce the new date format
                updateExpiryDate(expirySelectedDate)
                expiryIsDialogOpen = false
            },
            expiryCalendar.get(Calendar.YEAR),
            expiryCalendar.get(Calendar.MONTH),
            expiryCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
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
                                        item = uiState.dealState.itemName,
                                        description = uiState.dealState.description,
                                        type = uiState.dealState.dealType!!,
                                        expiryDate = getExpiryTimestamp(uiState.dealState.expiryDate),
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
                .padding(top = 40.dp)
                .verticalScroll(scrollState)
        ) {

            HidableSection(
                showContentWhenChecked = false,
                isChecked = true,
                title = "When can you get the deal?",
                label = "Anytime, anyday!",
            ) {
                TimeSelector(
                    labels = listOf("M", "T", "W", "Th", "F", "S", "Sun"),
                    updateStartTime = updateStartTimes,
                    updateEndTime = updateEndTimes,
                )
            }

            SectionDivider(
                modifier = Modifier
            )

            HidableSection(
                showContentWhenChecked = false,
                isChecked = true,
                title = "Who can get the deal?",
                label = "Open to all!",
            ) {
                Column (
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CustomCheckBox(
                        label = "Students"
                    )
                    CustomCheckBox(
                        label = "Children"
                    )
                    CustomCheckBox(
                        label = "Seniors"
                    )
                    CustomCheckBox(
                        label = "New customers"
                    )
                }
            }

            SectionDivider(
                modifier = Modifier
            )

            // TextField for displaying expiry date
            TitledOutlinedTextField(
                value = uiState.dealState.expiryDate ?: "",
                onValueChange = {},
                label = "Expiry Date",
                text = null,
                placeholder = "DD/MM/YYYY",
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expiryIsDialogOpen = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                    }
                },
                maxLines = 1,
            )
        }
    }
}

fun getExpiryTimestamp(expirySelectedDate: String?): Long? {
    expirySelectedDate ?: return null
    try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.parse(expirySelectedDate)?.time
    } catch (e : Exception) {
        return null
    }
}

