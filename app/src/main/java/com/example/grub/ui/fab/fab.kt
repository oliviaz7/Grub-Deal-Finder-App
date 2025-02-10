package com.example.grub.ui.fab

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.grub.R
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.CircleShape


@Composable
fun Fab(modifier: Modifier = Modifier) {
    // Add the FloatingActionButton
    FloatingActionButton(
        onClick = { println("CLICKED BUTTON") },
        modifier = modifier.padding(16.dp),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.addDealTooltip))
    }
}