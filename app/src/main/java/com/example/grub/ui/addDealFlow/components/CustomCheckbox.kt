package com.example.grub.ui.addDealFlow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun CustomCheckBox(
    modifier: Modifier = Modifier,
    label: String,
){
    var checked by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .padding(6.dp)
        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
        .height(40.dp)
        .fillMaxWidth(0.9f)
        .background(if (checked) MaterialTheme.colorScheme.primary else Color.White, RoundedCornerShape(5.dp))
        .clickable {
            checked = !checked
        },

        contentAlignment = Alignment.Center
    ){
        Text(
            text = label,
            fontSize = 15.sp,
            color = if (checked) Color.White else MaterialTheme.colorScheme.primary
        )

    }
}