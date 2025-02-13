package com.example.grub.ui.permissions

import android.Manifest
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(onPermissionResult: (Boolean) -> Unit) {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    var showRationale by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        println("RequestLocationPermission\n")
        // Check if permissions are already granted
        if (locationPermissionState.allPermissionsGranted) {
            onPermissionResult(true)
        } else {
            // If not, request permissions
            if (locationPermissionState.shouldShowRationale) {
                showRationale = true
            } else {
                locationPermissionState.launchMultiplePermissionRequest()
            }
        }
    }

    // Show rationale dialog if needed
    if (showRationale) {
        PermissionRationaleDialog(
            onDismiss = { showRationale = false },
            onContinue = {
                locationPermissionState.launchMultiplePermissionRequest()
                showRationale = false
            }
        )
    }

    // Listen for permission changes
    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        Log.d("location-permission", "locationPermissionState.allPermissionsGranted")
        onPermissionResult(locationPermissionState.allPermissionsGranted)
    }
}

@Composable
fun PermissionRationaleDialog(onDismiss: () -> Unit, onContinue: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Location Permission Required") },
        text = { Text("This app needs location permission to show your location on the map. Would you like to continue?") },
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