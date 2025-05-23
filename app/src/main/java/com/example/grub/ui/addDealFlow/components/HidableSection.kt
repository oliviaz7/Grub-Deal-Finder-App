package com.example.grub.ui.addDealFlow.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HidableSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    label: String = "",
    showContentWhenChecked: Boolean = true,
    checked: Boolean = false,
    onCheckedChanged: (Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column (
        modifier = modifier
    ) {
        title?.let {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    onCheckedChanged(it)
                },
                colors = CheckboxDefaults.colors(
//                    checkedColor = Color.Blue,
                    uncheckedColor = Color.Gray
                )
            )
            Text(text = label)
        }

        if (checked == showContentWhenChecked) {
            content()
        }
    }

}