//import android.graphics.Color
import androidx.compose.ui.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.grub.ui.map.MapUiState
import com.example.grub.ui.permissions.RequestLocationPermission
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.maps.android.compose.rememberMarkerState

import androidx.compose.runtime.LaunchedEffect
import com.google.android.gms.maps.CameraUpdateFactory

@Composable
fun MapScreen(
    uiState: MapUiState,
    onPermissionsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    RequestLocationPermission { granted ->
        onPermissionsChanged(granted)
    }

    Log.d("user-location", "right before we assign the camera, we have: ${uiState.userLocation}")

//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(
//            uiState.userLocation, 20f
//        )
//    }

    val zoomIn = 18f

    val cameraPositionState = rememberCameraPositionState {
        position = uiState.userLocation?.let {
            CameraPosition.fromLatLngZoom(it, zoomIn)
        } ?: CameraPosition.fromLatLngZoom(LatLng(56.13, 106.34), zoomIn)
    }

    // keep a reference to the underlying GoogleMap object
//    val googleMapRef = remember { mutableStateOf<GoogleMap?>(null) }

    // Animate the camera only when we get a non-null userLocation.
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, zoomIn)
            )
        }
    }


    // Animate the camera when the user location in the UI state changes.
//    LaunchedEffect(uiState.userLocation) {
//        cameraPositionState.animate(
//            update = CameraUpdateFactory.newLatLngZoom(uiState.userLocation, 20f)
//        )
//    }

    println("MAP SCREEN ui state: ${uiState.restaurantDeals}")

    // fused location client giving two locations
    // https://developer.android.com/develop/sensors-and-location/location/retrieve-current
    // use getCurrentLocation()?

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = uiState.hasLocationPermission),
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            // log the length of uiState.restaurantDeals
            Log.d("marker-bocation", "uiState.restaurantDeals.size: ${uiState.restaurantDeals.size}")

            // so question

            uiState.restaurantDeals.forEach { deal ->
                Log.d("marker-bocation", "deal: $deal")
                Marker(
                    state = rememberMarkerState(position = deal.coordinates),
                    title = deal.restaurantName
//                    snippet = deal.dealDescription
                )
            }
        }
    }
}