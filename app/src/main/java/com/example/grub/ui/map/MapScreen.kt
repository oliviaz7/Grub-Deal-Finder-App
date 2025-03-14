package com.example.grub.ui.map

import RestaurantItem
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.model.RestaurantDeal
import com.example.grub.ui.permissions.RequestLocationPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

object MapConfig {
    const val INITIAL_ZOOM_IN = 18f
    val INITIAL_POSITION_WHEN_LOADING = LatLng(43.47229330556622, -80.54489001631)

    const val ZOOM_OUT_BOUNDARY = 9f
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun MapScreen(
    uiState: MapUiState,
    navController: NavController,
    onPermissionsChanged: (Boolean) -> Unit,
    onMapEvent: (MapEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    RequestLocationPermission { granted -> onPermissionsChanged(granted) }

    val cameraPositionState = rememberCameraPositionState {
        position = uiState.userLocation?.let {
            CameraPosition.fromLatLngZoom(it, MapConfig.INITIAL_ZOOM_IN)
        } ?: CameraPosition.fromLatLngZoom(
            MapConfig.INITIAL_POSITION_WHEN_LOADING,
            MapConfig.INITIAL_ZOOM_IN
        )
    }

    var initialCameraAnimated by remember { mutableStateOf(false) }

    // Bottom sheet view
    val sheetState = rememberModalBottomSheetState()
    var selectedRestaurant by remember { mutableStateOf<RestaurantDeal?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // TODO: OLIVIA IMPLEMENT CUSTOM ICONS
    // TODO: OLIVIA FIX THE NAV BUG THAT CAUSES MAP TO RECENTER
    val markerIcon by lazy { BitmapDescriptorFactory.defaultMarker(21F) }
    // https://developer.android.com/develop/sensors-and-location/location/retrieve-current
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = uiState.hasLocationPermission),
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            onMapLoaded = {
                Log.d("marker-location", "google map loaded")
            },
        ) {
            if (cameraPositionState.position.zoom > MapConfig.ZOOM_OUT_BOUNDARY) {
                uiState.restaurantDeals.forEach { deal ->
                    key(deal.id) {  // Each marker is keyed uniquely, solve the randomly disappearing markers issue
                        Marker(
                            state = rememberMarkerState(position = deal.coordinates),
                            icon = markerIcon,
                            title = deal.restaurantName,
                            onClick = { _ ->
                                selectedRestaurant = deal
                                showBottomSheet = true
                                scope.launch { sheetState.show() }
                                true
                            }
                        )
                    }
                }
            }
        }

        if (showBottomSheet && selectedRestaurant != null) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 1f),
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                        selectedRestaurant = null
                    }
                },
                dragHandle = null,
                sheetState = sheetState
            ) {
                selectedRestaurant?.let { restaurant ->
                    RestaurantItem(
                        restaurant = restaurant,
                        navController = navController,
                        showBoxShadow = false,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }

    // Show a message to the user to zoom in if they are zoomed out too far
    if (cameraPositionState.position.zoom <= MapConfig.ZOOM_OUT_BOUNDARY && initialCameraAnimated) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Please zoom in",
                    color = Color.White
                )
            }
        }
    }

    // Show a loading spinner when we are waiting for the user's location
    if (uiState.loadingUserLocation) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                strokeWidth = 4.dp,
            )
        }
    }

    // Animate the camera when we get a non-null userLocation on the initial load
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let { location ->
            if (!initialCameraAnimated) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(location, MapConfig.INITIAL_ZOOM_IN)
                )
                // only animate the first time
                initialCameraAnimated = true
            }
        }
    }

    // Recalculate the visible radius when the camera stops moving (e.g. after zooming/panning).
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .distinctUntilChanged()
            // Only when the camera becomes idle.
            .filter { isMoving -> !isMoving }
            // If multiple events occur within 300ms, only the last one will be processed.
            .debounce(300)
            .collect {
                cameraPositionState.projection?.let { projection ->
                    onMapEvent(
                        MapEvent.UpdateCameraViewState(
                            cameraPositionState.position.target,
                            cameraPositionState.position.zoom,
                            getVisibleRadius(projection)
                        )
                    )
                    Log.d("marker-location", "after movement")
                }
            }
    }
}