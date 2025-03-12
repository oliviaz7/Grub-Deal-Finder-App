package com.example.grub.ui.addDealFlow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SectionDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        thickness = 1.dp
    )
}