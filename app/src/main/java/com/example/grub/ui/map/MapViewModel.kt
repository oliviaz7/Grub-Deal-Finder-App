package com.example.grub.ui.map

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.Result
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.mappers.RestaurantDealMapper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * event: ui --> viewmodel
 */
sealed class MapEvent {
    data class UpdateCameraViewState(val cameraCoordinate: LatLng?, val zoom: Float, val visibleRadius: Double?) : MapEvent()
}

/**
 * UI state for the Map route.
 */
data class MapUiState(
    val restaurantDeals: List<RestaurantDeal>,
    val hasLocationPermission: Boolean,
    val userLocation: LatLng?,
    val cameraCoordinate: LatLng?,
    val cameraZoom: Float,
    val visibleRadius: Double?
)

/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class MapViewModel(
    private val restaurantDealsRepository: RestaurantDealsRepository,
    private val dealMapper: RestaurantDealMapper,
    private val fusedLocationProviderClient : FusedLocationProviderClient
) : ViewModel() {

    // Mutable state that view model operations should update
    private val _uiState = MutableStateFlow(
        MapUiState(
            restaurantDeals = emptyList(),
            hasLocationPermission = false,
            userLocation = null,
            cameraCoordinate = null,
            cameraZoom = 0f,
            visibleRadius = null
        )
    )

    // UI state exposed to the UI (not mutable for safety)
    val uiState = _uiState.asStateFlow()

    private suspend fun getRestaurantDeals() {
        if (_uiState.value.cameraCoordinate != null && _uiState.value.visibleRadius != null) {
                // neither of them are null, but we still need to force?
            Log.d("marker-location", "cameracoord param for getRestaurantDeals: ${_uiState.value.cameraCoordinate}")
            Log.d("marker-location", "visibleRadius param for getRestaurantDeals: ${_uiState.value.visibleRadius}")
            restaurantDealsRepository.getRestaurantDeals(_uiState.value.cameraCoordinate!!, _uiState.value.visibleRadius!!).let { result ->
                when (result) {
                    is Result.Success -> {
                        val deals = result.data.map(dealMapper::mapResponseToRestaurantDeals)
                        _uiState.update { it.copy(restaurantDeals = deals) }
                        Log.d("marker-location", "restaurantDeals: ${_uiState.value.restaurantDeals}")
                        Log.d("marker-location", "restaurantDeals size: ${_uiState.value.restaurantDeals.size}")
                    }
                    else -> Log.e("FetchingError", "MapViewModel, request failed")
                }
            }
        } else {
            Log.e("marker-location", "_uiState.value.cameraCoordinate is null or _uiState.value.visibleRadius is null")
        }
    }

    // do we need to getRestaurantDeals on init
    init {
        viewModelScope.launch {
            getRestaurantDeals()
        }
    }

    /**
     * Update our own state when we detect a change in location permissions
     */
    fun onPermissionsChanged(hasLocationPermission: Boolean) {
        Log.i("location-permission", "location granted: $hasLocationPermission")
        _uiState.update {
            it.copy(hasLocationPermission = hasLocationPermission)
        }

        if (hasLocationPermission) { // and there was no previous location?
            getCurrentUserLocation()  // get the user location if permission is granted
        }
    }

    /**
     * Fetches the last known location and updates the UI state.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentUserLocation() {
        if (!_uiState.value.hasLocationPermission) {
            Log.e("user-location", "Permission denied, cannot fetch location")
            return
        }

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    _uiState.update { state -> state.copy(userLocation = latLng) }
                    Log.d("user-location", "Current user location: $latLng")
                } ?: Log.e("user-location", "Failed to get current location")
            }
    }

    fun onEvent(event: MapEvent) {
        Log.d("marker-location", "hello we in the function onEvent")
        when (event) {
            is MapEvent.UpdateCameraViewState -> {
                Log.d("marker-location", "event.visibleRadius: ${event.visibleRadius}")
                Log.d("marker-location", "event.cameraCoordinate: ${event.cameraCoordinate}")
                Log.d("marker-location", "event.zoom: ${event.zoom}")
                _uiState.value = _uiState.value.copy(
                    visibleRadius = event.visibleRadius,
                    cameraCoordinate = event.cameraCoordinate,
                    cameraZoom = event.zoom
                )
                if (event.zoom > 9f) { // turn this into a constant
                    viewModelScope.launch { getRestaurantDeals() }
                } else {
                    // Clear any existing deals when zoomed out too far
                    _uiState.update { current -> current.copy(restaurantDeals = emptyList()) }
                    Log.i("marker-location", "Zoom too low, skipping backend query")
                }
            }
        }
    }

    // https://developer.android.com/training/location/receive-location-updates
    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            restaurantDealsRepository: RestaurantDealsRepository,
            dealMapper: RestaurantDealMapper,
            fusedLocationProviderClient : FusedLocationProviderClient
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(restaurantDealsRepository, dealMapper, fusedLocationProviderClient) as T
            }
        }
    }
}
