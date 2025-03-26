package com.example.grub.ui.addDealFlow.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.grub.ui.addDealFlow.AddDealUiState
import com.example.grub.ui.addDealFlow.Step
import com.example.grub.ui.addDealFlow.components.CameraCaptureScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddImagesScreen(
    uiState: AddDealUiState,
    navController: NavController,
    uploadImageToFirebase: (imageUri: Uri) -> Unit,
    updateAndroidImageUri: (Uri?) -> Unit,
    onPermissionsChanged: (Boolean) -> Unit,
    prevStep: (Step?) -> Unit,
    nextStep: (Step?) -> Unit,
    modifier: Modifier = Modifier,
) {

    fun updateImage(uri: Uri?) {
        updateAndroidImageUri(uri)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        Log.d("ImagePickerButton", "ImagePickerButton Image URI: $uri")
        updateImage(uri)
    }

    var openCamerPreview by remember { mutableStateOf(false) }

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
                        onClick = { prevStep(Step.Step1) },
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
                OutlinedButton(
                    onClick = {
                        updateAndroidImageUri(null)
                        nextStep(null)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                ) {
                    Text("Skip")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        uiState.imageUri?.let { uri ->
                            // uploads image to firebase and saves the image key in the deal state
                            uploadImageToFirebase(uri)
                        }
                        nextStep(null)
                    },
                    enabled = uiState.imageUri != null
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
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (openCamerPreview) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CameraCaptureScreen(
                        updateImageUri = { updateImage(it) },
                        onPermissionsChanged = onPermissionsChanged,
                        onDismiss = { openCamerPreview = false },
                    )
                }
            } else {
                Text(
                    text = "Upload an image to share!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                ImagePickerButton(
                    onClick = { launcher.launch("image/*") },
                    imageUri = uiState.imageUri
                )
            }

            Button(
                onClick = { openCamerPreview = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Open Camera",
                )
            }
        }
    }
}

@Composable
fun ImagePickerButton(imageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(
                if (imageUri == null) 300.dp else 400.dp
            )
            .background(
                if (imageUri == null) Color.LightGray else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
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