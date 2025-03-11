package com.example.grub.ui.addDealFlow.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.ui.addDealFlow.AddDealUiState
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.layout.ContentScale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddImagesScreen (
    uiState: AddDealUiState,
    navController: NavController,
    uploadImage: (imageUri: Uri) -> Unit,
    updateImageUri: (Uri?) -> Unit,
    prevStep: () -> Unit,
    nextStep: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        Log.d("ImagePickerButton", "ImagePickerButton Image URI: ${uri}")
        updateImageUri(uri)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Add Images")
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
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
                    onClick = {
                        uiState.imageUri?.let { uri ->
                            uploadImage(uri)
                        }
                        nextStep()
                    },
                ) {
                    Text("Next")
                }
                Button(
                    onClick = { prevStep() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary
                    ),
                ) {
                    Text("Previous")
                }
            }
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImagePickerButton(
                onClick = {launcher.launch("image/*") },
                imageUri = uiState.imageUri
            )
        }



    }
}

@Composable
fun ImagePickerButton(imageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(300.dp)
            .background(
                Color.LightGray,
                shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,

    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    tint = Color.White,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}