package com.example.grub.ui.addDealFlow.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
            Text(text = label, style = MaterialTheme.typography.titleLarge)
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