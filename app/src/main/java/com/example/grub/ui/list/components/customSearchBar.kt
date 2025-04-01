package com.example.grub.ui.list.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onFilter: () -> Unit,
    interactionSource: MutableInteractionSource? = null,
) {
    LaunchedEffect(searchText) {
        delay(500)
        onFilter()
    }

    BasicTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        textStyle = LocalTextStyle.current,
        modifier = modifier,
        interactionSource = interactionSource,
        enabled = true,
        singleLine = true,
    ) { innerTextField ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray,
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text Field with Placeholder
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchText.isEmpty()) {
                    Text(
                        text = "Search",
                        color = Color.Gray,
                    )
                }
                innerTextField()
            }
        }
    }
}