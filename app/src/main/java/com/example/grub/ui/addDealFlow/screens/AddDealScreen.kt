package com.example.grub.ui.addDealFlow.screens

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDealScreen(
    uiState: AddDealUiState,
    navController: NavController,
    uploadImage: (imageUri: Uri) -> Unit,
    addNewRestaurantDeal: (RestaurantDealsResponse) -> Unit,
    searchNearbyRestaurants: (keyword: String, coordinates: LatLng, radius: Double) -> Unit,
    prevStep: () -> Unit,
    nextStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    println("Select restaurant: ${uiState.deals}")

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    var itemName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedDealType by remember { mutableStateOf(DealType.OTHER) }
    val dealTypes = DealType.values().toList()

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
                && description.isNotEmpty()
                && uiState.selectedRestaurant.placeId.isNotEmpty()
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
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
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
                                    RawDeal(
                                        id = "default_deal_id",
                                        item = itemName,
                                        description = description,
                                        type = selectedDealType,
                                        expiryDate = getExpiryTimestamp(expirySelectedDate),
                                        datePosted = System.currentTimeMillis(),
                                        userId = "default_user_id",
                                        restrictions = "None",
                                        imageId = imageUri?.path, // idk if this is right
                                    )
                                )
                            )
                        )
                        nextStep()
                    },
                ) {
                    Text("Submit")
                }
                Button(
                    onClick = { prevStep() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary
                    ),
                ) {
                    Text("Previous")
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
            Text(text = uiState.selectedRestaurant.restaurantName)
            Button(
                onClick = { launcher.launch("image/*") }
            ) {
                Text("Open image picker")
            }

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = selectedDealType.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Deal Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(), // Make TextField clickable,
                    maxLines = 1,
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
            OutlinedTextField(
                value = expirySelectedDate,
                onValueChange = {},
                label = { Text("Expiry Date (optional)") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expiryIsDialogOpen = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            imageUri?.let { uri ->
                Button(
                    onClick = { uploadImage(uri) }
                ) {
                    Text("Upload Image Test")
                }
            }
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
