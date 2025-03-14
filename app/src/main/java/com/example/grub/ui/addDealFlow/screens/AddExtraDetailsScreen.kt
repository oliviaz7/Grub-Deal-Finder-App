package com.example.grub.ui.addDealFlow.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.grub.data.Result
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.model.ApplicableGroup
import com.example.grub.ui.addDealFlow.AddDealUiState
import java.util.Calendar
import com.example.grub.ui.addDealFlow.components.TimeSelector
import com.example.grub.ui.addDealFlow.components.ConfirmationDialog
import com.example.grub.ui.addDealFlow.components.CustomRadioButton
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
    updateApplicableGroups: (ApplicableGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // hidable section states
    var isTimeSelectorChecked by remember { mutableStateOf(true) }
    var isApplicableGroupChecked by remember { mutableStateOf(true) }


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

    if (showErrorDialog.isNotEmpty()) {
        ConfirmationDialog(
            navController = navController,
            result = Result.Error(Exception(showErrorDialog)),
            onDismiss = { showErrorDialog = "" }
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


    fun isValidTimeRange() : Boolean {
        val startTimes = uiState.dealState.startTimes
        val endTimes = uiState.dealState.endTimes
        if (startTimes.size != 7 || endTimes.size != 7) {
            return false
        }
        for (i in startTimes.indices) {
            // both must be -1 or both must be >= 0 and start time < end time
            if (startTimes[i] == -1 && endTimes[i] == -1) {
                continue
            } else if (startTimes[i] < 0 || endTimes[i] < 0 || startTimes[i] > 24 * 60 || endTimes[i] > 24 * 60) {
                return false
            } else if (startTimes[i] >= endTimes[i]) {
                return false
            }
        }
        if (startTimes.all {it == -1} && endTimes.all {it == -1}) {
            return false
        }
        return true
    }

    fun errorCheck() {
        if (!isValidTimeRange()) {
            Log.d("AddExtraDetailsScreen", "Start time: ${uiState.dealState.startTimes}, End time: ${uiState.dealState.endTimes}")
            throw Exception("Invalid time range")
        }
    }

    fun tryAddNewRestaurantDeal () {
        try {
            errorCheck()
        } catch (e: Exception) {
            showErrorDialog = e.message ?: "Unknown error"
            return
        }
        addNewRestaurantDeal(
            RestaurantDealsResponse(
                id = "default_id",
                placeId = uiState.selectedRestaurant.placeId,
                coordinates = uiState.selectedRestaurant.coordinates,
                restaurantName = uiState.selectedRestaurant.restaurantName,
                displayAddress = "restaurant_addy", // will be added in the BE
                rawDeals = listOf(
                    RawDeal( // TODO: add price to raw deal obj
                        id = "default_deal_id",
                        item = uiState.dealState.itemName,
                        description = uiState.dealState.description,
                        type = uiState.dealState.dealType!!,
                        expiryDate = getExpiryTimestamp(uiState.dealState.expiryDate),
                        datePosted = System.currentTimeMillis(),
                        userId = uiState.userId,
                        restrictions = "None",
                        imageId = uiState.imageUri?.path, // idk if this is right,
                        applicableGroup = uiState.dealState.applicableGroup,
                        dailyStartTimes = uiState.dealState.startTimes,
                        dailyEndTimes = uiState.dealState.endTimes,
                    )
                )
            )
        )
        showDialog = true
    }

    val applicableGroups = ApplicableGroup.entries.filter { it.toString().isNotEmpty() }

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
                    onClick = { tryAddNewRestaurantDeal() },
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

            // time selector
            HidableSection(
                showContentWhenChecked = false,
                checked = isTimeSelectorChecked,
                title = "When can you get the deal?",
                label = "Anytime, anyday!",
                onCheckedChanged =
                    { isTimeSelectorChecked = it
                      if (isTimeSelectorChecked) {
                        updateStartTimes(emptyList())
                        updateEndTimes(emptyList())
                        }
                    },
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

            // applicable groups
            HidableSection(
                showContentWhenChecked = false,
                checked = isApplicableGroupChecked,
                title = "Who can get the deal?",
                label = "Open to all!",
                onCheckedChanged = {
                    isApplicableGroupChecked = it
                    if (isApplicableGroupChecked) {
                        updateApplicableGroups(ApplicableGroup.ALL)
                    } else {
                        updateApplicableGroups(ApplicableGroup.UNDER_18)
                    } },
            ) {
                Column (
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    applicableGroups.forEach { group ->
                        CustomRadioButton(
                            label = group.toString(),
                            isChecked = uiState.dealState.applicableGroup == group,
                            onChange = { updateApplicableGroups(group) } // if isChecked -> add
                        )
                    }
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

