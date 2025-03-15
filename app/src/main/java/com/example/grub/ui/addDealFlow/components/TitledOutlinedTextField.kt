package com.example.grub.ui.addDealFlow.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
        modifier = modifier.padding(8.dp)
    ) {
        Row {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge
            )
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
            modifier = Modifier.padding(0.dp, 5.dp).fillMaxWidth(),
            trailingIcon = trailingIcon,
            maxLines = maxLines,
            readOnly = readOnly,
        )
    }
}
