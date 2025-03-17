package com.example.grub.ui.addDealFlow.screens

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.grub.ui.addDealFlow.AddDealUiState


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddImagesScreen(
    uiState: AddDealUiState,
    navController: NavController,
    uploadImageToFirebase: (imageUri: Uri) -> Unit,
    updateAndroidImageUri: (Uri?) -> Unit,
    updateImageExtension: (String) -> Unit,
    prevStep: () -> Unit,
    nextStep: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentResolver: ContentResolver = LocalContext.current.contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        Log.d("ImagePickerButton", "ImagePickerButton Image URI: $uri")
        updateAndroidImageUri(uri)
        uri?.let {
            contentResolver.getType(it)
            val extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
            updateImageExtension(extension ?: "")
        }
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
                OutlinedButton(
                    onClick = {
                        updateAndroidImageUri(null)
                        nextStep()
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
                        nextStep()
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


    }
}

@Composable
fun ImagePickerButton(imageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(300.dp)
            .background(
                Color.LightGray,
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