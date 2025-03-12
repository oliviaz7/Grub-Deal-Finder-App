package com.example.grub.ui.addDealFlow.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeSelector(
    modifier: Modifier = Modifier,
    labels: List<String>, // corresponds to the number of buttons
    updateStartTime: (List<Int>) -> Unit,
    updateEndTime: (List<Int>) -> Unit,
) {
    val isToggledList = remember { mutableStateListOf(*Array(labels.size) { true }) }
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
            content = {
                // TODO: Add time picker
            },
            showContentWhenChecked = false,
            isChecked = true,
            label = "All day",
        )
    }
}