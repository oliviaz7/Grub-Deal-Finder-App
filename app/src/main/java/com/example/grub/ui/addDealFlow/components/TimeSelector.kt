package com.example.grub.ui.addDealFlow.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelector(
    modifier: Modifier = Modifier,
    labels: List<String>, // corresponds to the number of buttons
    updateStartTime: (List<Int>) -> Unit,
    updateEndTime: (List<Int>) -> Unit,
) {
    val isToggledList = remember { mutableStateListOf(*Array(labels.size) { false }) }

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

    LaunchedEffect(startTimePickerState.hour, startTimePickerState.minute) {
        val startTimes = isToggledList.map {
            if (it) startTimePickerState.hour * 60 + startTimePickerState.minute else -1
        }
        updateStartTime(startTimes)
    }

    LaunchedEffect(endTimePickerState.hour, endTimePickerState.minute) {
        val endTimes = isToggledList.map {
            if (it) endTimePickerState.hour * 60 + endTimePickerState.minute else -1
        }
        updateEndTime(endTimes)
    }

    // if the day is toggled, set the start time to 0 and end time to 24 * 60
    // otherwise, make the deal unavailable all day (-1, -1)
    fun allDayCheck(isChecked : Boolean) {
        if (isChecked) {
            val startTimes = isToggledList.map { if (it) 0 else -1 }
            val endTimes = isToggledList.map { if (it) 24 * 60 else -1 }
            updateStartTime(startTimes)
            updateEndTime(endTimes)
        }
    }

    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (i in labels.indices) {
                Button(
                    onClick = { isToggledList[i] = !isToggledList[i] },
                    modifier = Modifier
                        .weight(1f) // Distribute buttons evenly
                        .aspectRatio(1f) // Make the button circular
                        .padding(6.dp) // Add padding around the button
                        .then(
                            if (!isToggledList[i]) Modifier.border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            ) else Modifier
                        ), // Conditionally add border
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
            isChecked = true,
            label = "All day",
            onClick = ::allDayCheck,
        ) {
            // TODO: Add time picker
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(0.dp),
                ){
                    Text(
                        text = "Start Time"
                    )
                    TimeInput(
                        state = startTimePickerState,
                        modifier = Modifier.scale(0.75f),
                    )

                }
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "End Time"
                    )
                    TimeInput(
                        state = endTimePickerState,
                        modifier = Modifier.scale(0.8f),
                    )

                }
            }

        }
    }
}