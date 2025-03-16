package com.example.grub.ui.addDealFlow.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.data.deals.DealIdResponse
import com.example.grub.data.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    navController: NavController,
    result: Result<DealIdResponse>?,
    onDismiss: () -> Unit,
    errorDialog: String = "",
    modifier: Modifier = Modifier,
) {
    val errorMessage = (result as? Result.Error)?.exception?.message ?: "Unknown error"

    when (result) {
        is Result.Success -> {
            AlertDialog(
                onDismissRequest = {
                    onDismiss()
                    navController.popBackStack()
                },
                title = { Text(text = "Confirmation") },
                text = { Text(text = "Your action was successful!") },
                confirmButton = {
                    Button(onClick = {
                        onDismiss()
                        navController.popBackStack()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
        is Result.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(text = "Error") },
                text = {
                    Text(text = "There was an error. Try again. \n Error: $errorMessage")
                       },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            )
        }
        else -> {
            // loading
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {},
                text = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                },
                modifier = Modifier.size(200.dp)
            )
        }
    }

}