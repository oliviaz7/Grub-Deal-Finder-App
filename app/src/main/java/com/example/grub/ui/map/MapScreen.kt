//import androidx.compose.foundation.layout.BoxScopeInstance.align

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.grub.ui.map.MapEvent
import com.example.grub.ui.map.MapUiState
import com.example.grub.ui.map.getVisibleRadius
import com.example.grub.ui.permissions.RequestLocationPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    uiState: MapUiState,
    navController: NavController,
    onPermissionsChanged: (Boolean) -> Unit,
    onEvent: (MapEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    RequestLocationPermission { granted -> onPermissionsChanged(granted) }
    val zoomIn = 18f

    val cameraPositionState = rememberCameraPositionState {
        position = uiState.userLocation?.let {
            CameraPosition.fromLatLngZoom(it, zoomIn)
        } ?: CameraPosition.fromLatLngZoom(LatLng(56.13, 106.34), zoomIn) // siberia
    }

    var initialCameraAnimated by remember { mutableStateOf(false) }

    // Bottom sheet view
    val sheetState = rememberModalBottomSheetState()
    var selectedRestaurant by remember { mutableStateOf<RestaurantDeal?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
            }
        ) {
            uiState.restaurantDeals.forEach { deal ->
                key(deal.id) {  // Each marker is keyed uniquely, solve the randomly disappearing markers issue
                    Log.d("marker-location", "deal: $deal")
                    Marker(
                        state = rememberMarkerState(position = deal.coordinates),
                        title = deal.restaurantName,
                        onClick = { _ ->
                            Log.d("marker-location", "clicked on marker: $deal")
                            selectedRestaurant = deal
                            showBottomSheet = true
                            scope.launch { sheetState.show() }
                            true
                        }
                    )
                }
            }
        }

        if (showBottomSheet && selectedRestaurant != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                        selectedRestaurant = null
                    }
                },
                sheetState = sheetState
            ) {
                selectedRestaurant?.let { restaurant ->
                    RestaurantItem(
                        restaurant = restaurant,
                        navController = navController,
                    )
                }
            }

        }
    }

    if (cameraPositionState.position.zoom <= 9f) {
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


    // Animate the camera when we get a non-null userLocation.
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let { location ->
            if (!initialCameraAnimated) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(location, zoomIn)
                )
                //
                onEvent(MapEvent.UpdateCameraViewState(
                    cameraPositionState.position.target, cameraPositionState.position.zoom, getVisibleRadius(cameraPositionState.projection))
                )
                Log.d("marker-location", "initial load after everything we need is not null")
                initialCameraAnimated = true
            }
        }
    }

    // Recalculate the visible radius when the camera stops moving (e.g. after zooming/panning).
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .distinctUntilChanged()
            .filter { isMoving -> !isMoving }  // Only when the camera becomes idle.
            .collect {
                cameraPositionState.projection?.let { _ ->
                    // Send an event to the caller.
                    onEvent(MapEvent.UpdateCameraViewState(
                        cameraPositionState.position.target, cameraPositionState.position.zoom, getVisibleRadius(cameraPositionState.projection))
                    )
                    Log.d("marker-location", "after movement")
                }
            }

    }
}