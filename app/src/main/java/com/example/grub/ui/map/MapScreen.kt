import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.grub.ui.map.MapUiState
import com.example.grub.ui.permissions.RequestLocationPermission
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    uiState: MapUiState,
    onPermissionsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    println("MAP SCREEN ui state: ${uiState.restaurantDeals}")

    RequestLocationPermission { granted ->
        onPermissionsChanged(granted)
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false
            )
        )
    }
}