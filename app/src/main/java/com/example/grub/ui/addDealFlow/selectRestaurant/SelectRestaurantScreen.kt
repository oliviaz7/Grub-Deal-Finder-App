package com.example.grub.ui.addDealFlow.selectRestaurant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun SelectRestaurantScreen(
    uiState: SelectRestaurantUiState,
    navController: NavController,
    uploadImage: (imageUri: Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    println("Select restaurant: ${uiState.deals}")

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Red),
    ) {
        Row(modifier = Modifier.align(Alignment.End)) {
            IconButton(
                onClick = { navController.popBackStack() },
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                )
            }
        }

        Button(
            onClick = { launcher.launch("image/*") }
        ) {
            Text("Open image picker")
        }

        imageUri?.let { uri ->
            Button(
                onClick = { uploadImage(uri) }
            ) {
                Text("Upload Image Test")
            }
        }
    }
}