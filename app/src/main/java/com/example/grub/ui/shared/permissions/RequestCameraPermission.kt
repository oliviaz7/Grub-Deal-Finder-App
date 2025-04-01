package com.example.grub.ui.shared.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.Manifest
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission(onPermissionResult: (Boolean) -> Unit) {
    var showRationale by remember { mutableStateOf(false) }
    var anyPermissionGranted by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    { permissionsResult ->
        onPermissionResult(permissionsResult)
    }

    LaunchedEffect(Unit) {
        // if we don't have any permissions granted, and haven't shown the dialog
        // show the request rational dialog
        if (!anyPermissionGranted && cameraPermissionState.status.shouldShowRationale) {
            showRationale = true
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Show rationale dialog if needed
    if (showRationale) {
        CameraPermissionRationaleDialog(
            onDismiss = { showRationale = false },
            onContinue = {
                cameraPermissionState.launchPermissionRequest()
                showRationale = false
            }
        )
    }

    // Listen for permission changes
    LaunchedEffect(cameraPermissionState.status.isGranted) {
        Log.d("camera-permission", "cameraPermissionState.status.isGranted")
        onPermissionResult(cameraPermissionState.status.isGranted)
    }
}

@Composable
fun CameraPermissionRationaleDialog(onDismiss: () -> Unit, onContinue: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Camera Permission Required") },
        text = { Text("This app would like to access your camera. Would you like to continue?") },
        confirmButton = {
            Button(onClick = onContinue) {
                Text("Continue")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}