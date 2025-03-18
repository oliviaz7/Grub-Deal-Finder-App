package com.example.grub.ui.addDealFlow.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.example.grub.model.NOT_AVAILABLE
import com.example.grub.model.mappers.MAX_MINUTES_IN_DAY
import com.example.grub.model.mappers.MIN_MINUTES_IN_DAY
import com.example.grub.ui.addDealFlow.AddDealUiState
import java.util.Calendar
import com.example.grub.ui.addDealFlow.components.TimeSelector
import com.example.grub.ui.addDealFlow.components.ConfirmationDialog
import com.example.grub.ui.addDealFlow.components.CustomRadioButton
import com.example.grub.ui.addDealFlow.components.TitledOutlinedTextField
import com.example.grub.ui.addDealFlow.components.HidableSection
import com.example.grub.ui.addDealFlow.components.SectionDivider
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExtraDetailsScreen(
    uiState: AddDealUiState,
    navController: NavController,
    addNewRestaurantDeal: () -> Unit,
    prevStep: () -> Unit,
    updateStartTimes: (List<Int>) -> Unit,
    updateEndTimes: (List<Int>) -> Unit,
    updateExpiryDate: (ZonedDateTime) -> Unit,
    updateApplicableGroups: (ApplicableGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // hide-able section states
    var isTimeSelectorChecked by remember { mutableStateOf(true) }
    var isApplicableGroupChecked by remember { mutableStateOf(true) }


    // State variables for Date Picker and selected date
    val expiryCalendar = Calendar.getInstance()
    var expiryIsDialogOpen by remember { mutableStateOf(false) }
    var expiryDateString by remember { mutableStateOf("") }

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
    // updates uiState expiry date and expiryDateString
    if (expiryIsDialogOpen) {
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                // Format selected date
                val localDate = LocalDate.of(year, month + 1, dayOfMonth)
                val zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault())
                expiryDateString = String.format(
                    "%02d/%02d/%04d",
                    dayOfMonth,
                    month + 1,
                    year
                )
                updateExpiryDate(zonedDateTime)
                expiryIsDialogOpen = false
            },
            expiryCalendar.get(Calendar.YEAR),
            expiryCalendar.get(Calendar.MONTH),
            expiryCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // this function might be buggy lolz
    fun isValidTimeRange() : Boolean {
        val startTimes = uiState.dealState.startTimes
        val endTimes = uiState.dealState.endTimes
        if (startTimes.size != 7 || endTimes.size != 7) {
            return false
        }
        for (i in startTimes.indices) {
            // both must be NOT_AVAILABLE or both must be >= 0 and start time < end time
            if (startTimes[i] == NOT_AVAILABLE && endTimes[i] == NOT_AVAILABLE) {
                continue
            } else if (startTimes[i] < MIN_MINUTES_IN_DAY || endTimes[i] < MIN_MINUTES_IN_DAY || startTimes[i] > MAX_MINUTES_IN_DAY || endTimes[i] > MAX_MINUTES_IN_DAY) {
                return false
            } else if (startTimes[i] >= endTimes[i]) {
                return false
            }
        }
        if (startTimes.all {it == NOT_AVAILABLE} && endTimes.all {it == NOT_AVAILABLE}) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    fun tryAddNewRestaurantDeal () {
        try {
            errorCheck()
        } catch (e: Exception) {
            showErrorDialog = e.message ?: "Unknown error"
            return
        }
        addNewRestaurantDeal() // restaurantDeal created in AddDealViewModel
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
        modifier = modifier,
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
                value = if (expiryDateString.isNotEmpty()) expiryDateString else "",
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
