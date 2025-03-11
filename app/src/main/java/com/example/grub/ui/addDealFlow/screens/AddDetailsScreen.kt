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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.widthIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDetailsScreen(
    uiState: AddDealUiState,
    navController: NavController,
    addNewRestaurantDeal: (RestaurantDealsResponse) -> Unit,
    prevStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    println("Select restaurant: ${uiState.deals}")

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
        ) {
            TitledOutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = "Item Name",
                text = null,
                placeholder = "E.g. Whopper Deal",
                optional = false,
                modifier = Modifier.fillMaxWidth()
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

            // TextField for displaying selected date
            TitledOutlinedTextField(
                value = expirySelectedDate,
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

fun getExpiryTimestamp(expirySelectedDate: String): Long? {
    try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.parse(expirySelectedDate)?.time
    } catch (e : Exception) {
        return null
    }
}

@Composable
fun TitledOutlinedTextField (
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    text: String?,
    placeholder: String,
    optional: Boolean = true,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    {
        Row {
            Text(text = label)
            if (!optional) {
                Text(text = " *", color = Color.Red)
            }
        }
        text?.let {
            Text(text = text)
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = modifier,
            trailingIcon = trailingIcon,
            maxLines = maxLines,
            readOnly = readOnly,
        )
    }
}

@Composable
fun DollarInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    text: String?,
    placeholder: String,
    optional: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val formattedValue = remember(value) {
        val regex = "^\\d*(\\.\\d{0,2})?$".toRegex()
        if (value.matches(regex)) value else value.dropLast(1)
    }
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row {
            Text(text = label)
            if (!optional) {
                Text(text = " *", color = Color.Red)
            }
        }
        text?.let {
            Text(text = text)
        }
        OutlinedTextField(
            value = formattedValue,
            onValueChange = { newValue ->
                if (newValue.matches("^\\d*(\\.\\d{0,2})?$".toRegex())) {
                    onValueChange(newValue)
                }
            },
            placeholder = { Text(placeholder) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text("$") },
            modifier = Modifier.widthIn(min = 10.dp),
            maxLines = 1,
        )
    }
}
