package com.example.grub.ui.addDealFlow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.model.DealType
import com.example.grub.ui.addDealFlow.AddDealUiState
import com.example.grub.ui.addDealFlow.components.DollarInputField
import com.example.grub.ui.addDealFlow.components.TitledOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDetailsScreen(
    uiState: AddDealUiState,
    navController: NavController,
    prevStep: () -> Unit,
    nextStep: () -> Unit,
    updateItemName: (String) -> Unit,
    updateDescription: (String?) -> Unit,
    updatePrice: (String?) -> Unit,
    updateDealType: (DealType) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    val dealTypes = DealType.entries.toList()

    fun isSubmitButtonEnabled(): Boolean {
        return uiState.selectedRestaurant.restaurantName.isNotEmpty()
                && uiState.dealState.itemName.isNotEmpty()
                && uiState.selectedRestaurant.placeId.isNotEmpty()
                && uiState.dealState.dealType !== null
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Add Details")
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = prevStep,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isSubmitButtonEnabled(),
                    onClick = nextStep,
                ) {
                    Text("Next")
                }
            }
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // text field for item name
            TitledOutlinedTextField(
                value = uiState.dealState.itemName,
                onValueChange = updateItemName,
                label = "Item Name",
                text = null,
                placeholder = "E.g. Whopper Deal",
                optional = false,
                modifier = Modifier.fillMaxWidth()
            )

            // textfield for description
            TitledOutlinedTextField(
                value = uiState.dealState.description ?: "",
                onValueChange = updateDescription,
                label = "Description",
                text = null,
                placeholder = "E.g. Whopper deal includes whopper, fries and drink",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
            )

            // TextField for price
            DollarInputField(
                value = uiState.dealState.price ?: "",
                onValueChange = updatePrice,
                label = "Price",
                text = null,
                placeholder = "0.00",
                modifier = Modifier.align(Alignment.Start)
            )

            // selecting deal type
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TitledOutlinedTextField(
                    value = uiState.dealState.dealType?.toString() ?: "",
                    onValueChange = {},
                    label = "Deal Type",
                    text = null,
                    placeholder = "Select a deal type",
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 0.dp),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    maxLines = 1,
                    optional = false,
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    dealTypes.forEach { dealType ->
                        DropdownMenuItem(
                            text = { Text(dealType.name) },
                            onClick = {
                                updateDealType(dealType)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


