package com.example.grub.ui.addDealFlow.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grub.model.NOT_AVAILABLE
import java.util.Calendar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelector(
    modifier: Modifier = Modifier,
    labels: List<String>, // corresponds to the number of buttons
    updateStartTime: (List<Int>) -> Unit,
    updateEndTime: (List<Int>) -> Unit,
) {
    val isToggledList = remember { mutableStateListOf<Boolean>().apply { addAll(List(labels.size) { false }) } }
    var isAllDayChecked by remember { mutableStateOf(true) }
    var isStartTimeDialogOpen by remember { mutableStateOf(false) }
    var isEndTimeDialogOpen by remember { mutableStateOf(false) }

    val currentTime = Calendar.getInstance()

    val startTimePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val endTimePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    fun _updateStartTime(hour: Int, minute: Int) {
        val startTimes = isToggledList.map {
            if (it) hour * 60 + minute else NOT_AVAILABLE
        }
        Log.d("TimeSelector", "Start times: $startTimes")
        updateStartTime(startTimes)
    }

    fun _updateEndTime(hour: Int, minute: Int) {
        val endTimes = isToggledList.map {
            if (it) hour * 60 + minute else NOT_AVAILABLE
        }
        Log.d("TimeSelector", "End times: $endTimes")
        updateEndTime(endTimes)
    }


    fun onUpdateAllDayCheck(isChecked : Boolean) {
        if (isChecked) { // if all day is checked, set the start time to 0 and end time to 24 * 60
            _updateStartTime(0, 0)
            _updateEndTime(24, 0)
        } else { // set the time to the time picker state
            _updateStartTime(startTimePickerState.hour, startTimePickerState.minute)
            _updateEndTime(endTimePickerState.hour, endTimePickerState.minute)
        }
    }

    if (isStartTimeDialogOpen) {
        TimePickerDialog(
            onConfirm = {
                onUpdateAllDayCheck(isAllDayChecked)
                isStartTimeDialogOpen = false
                        },
            onDismiss = { isStartTimeDialogOpen = false },
            timePickerState = startTimePickerState,
        )
    }

    if (isEndTimeDialogOpen) {
        TimePickerDialog(
            onConfirm = {
                onUpdateAllDayCheck(isAllDayChecked)
                isEndTimeDialogOpen = false
                        },
            onDismiss = { isEndTimeDialogOpen = false },
            timePickerState = endTimePickerState,
        )
    }

    fun formatTime (timePickerState: TimePickerState) : String {
        val formattedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
        return formattedTime

    }

    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (i in labels.indices) {
                Button(
                    onClick = {
                        isToggledList[i] = !isToggledList[i]
                        onUpdateAllDayCheck(isAllDayChecked)
                              },
                    modifier = Modifier
                        .weight(1f) // Distribute buttons evenly
                        .aspectRatio(1f) // Make the button circular
                        .padding(6.dp), // Add padding around the button
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp), // Remove inner padding
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isToggledList[i]) MaterialTheme.colorScheme.primary
                        else Color.White,
                        contentColor = if (isToggledList[i]) Color.White
                        else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = labels[i],
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.CenterVertically) // Center the label
                    )
                }
            }
        }
        HidableSection(
            showContentWhenChecked = false,
            checked = isAllDayChecked,
            label = "All day",
            onCheckedChanged = {
                isAllDayChecked = it
                onUpdateAllDayCheck(isAllDayChecked)
                },
        ) {
            // TODO: Add time picker

            TitledOutlinedTextField(
                value = formatTime(startTimePickerState),
                onValueChange = {},
                label = "Start Date",
                text = null,
                placeholder = "--:--",
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { isStartTimeDialogOpen = true }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Select Start Time")
                    }
                },
                maxLines = 1,
            )

            TitledOutlinedTextField(
                value = formatTime(endTimePickerState),
                onValueChange = {},
                label = "End Date",
                text = null,
                placeholder = "--:--",
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { isEndTimeDialogOpen = true }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Select End Time")
                    }
                },
                maxLines = 1,
            )
        }
    }
}