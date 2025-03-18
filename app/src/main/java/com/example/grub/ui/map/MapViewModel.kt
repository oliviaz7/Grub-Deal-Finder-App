package com.example.grub.ui.map

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.User
import com.example.grub.model.mappers.RestaurantDealMapper
import com.example.grub.ui.AppViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * event: ui --> viewmodel
 */
sealed class MapEvent {
    data class UpdateCameraViewState(
        val cameraCoordinate: LatLng?,
        val zoom: Float,
        val visibleRadius: Double?
    ) : MapEvent()
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
    val visibleRadius: Double?,
    val currUser: User?,
) {
    val loadingUserLocation = hasLocationPermission && userLocation == null
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class MapViewModel(
    private val restaurantDealsRepository: RestaurantDealsRepository,
    private val dealMapper: RestaurantDealMapper,
    private val appViewModel: AppViewModel,
) : ViewModel() {

    // Mutable state that view model operations should update
    private val _uiState = MutableStateFlow(
        MapUiState(
            restaurantDeals = emptyList(),
            hasLocationPermission = false,
            userLocation = null,
            cameraCoordinate = null,
            cameraZoom = 0f,
            visibleRadius = null,
            currUser = null,
        )
    )

    // UI state exposed to the UI (not mutable for safety)
    val uiState = _uiState.asStateFlow()

    private suspend fun getRestaurantDeals() {
        val currentCameraCoordinates = _uiState.value.cameraCoordinate
        val currentVisibleRadius = _uiState.value.visibleRadius
        val currUser = _uiState.value.currUser
        Log.d(
            "marker-location",
            "getRestaurantDeals camera coords param: $currentCameraCoordinates and visibleRadius: $currentVisibleRadius"
        )

        if (currentCameraCoordinates != null && currentVisibleRadius != null) {
            restaurantDealsRepository.getRestaurantDeals(
                currentCameraCoordinates,
                currentVisibleRadius,
                currUser?.id
            )
        }
    }

    init {
        // subscribe to changes in the accumulated restaurant deals
        viewModelScope.launch {
            getRestaurantDeals()
            restaurantDealsRepository.accumulatedDeals().collect { accumulatedDeals ->
                val mappedDeals =
                    accumulatedDeals.map { dealMapper.mapResponseToRestaurantDeals(it) }
                _uiState.update { it.copy(restaurantDeals = mappedDeals) }
            }
        }
        // subscribe to changes in the users location
        viewModelScope.launch {
            appViewModel.currentUserLocation.collect { coordinates: LatLng? ->
                if (coordinates != _uiState.value.userLocation) {
                    _uiState.update {
                        _uiState.value.copy(userLocation = coordinates)
                    }
                }
            }
        }
        // subscribe to changes in the users location permission
        viewModelScope.launch {
            appViewModel.hasLocationPermission.collect { permissionGranted: Boolean ->
                if (permissionGranted != _uiState.value.hasLocationPermission) {
                    _uiState.update {
                        _uiState.value.copy(hasLocationPermission = permissionGranted)
                    }
                }
            }
        }
        viewModelScope.launch {
            appViewModel.currentUser.collect { currentUser: User? ->
                _uiState.update {
                    it.copy(
                        currUser = currentUser,
                    )
                }
            }
        }
    }

    fun onMapEvent(event: MapEvent) {
        when (event) {
            // TODO: DEBOUNCE THE CAMERA VIEW STATE AND ONLY UPDATE ONCE THE USER STOPS MOVING
            // TODO: ADD A FUNCTION TO APP VIEW MODEL TO KEEP THE CURRENT FOCAL CAMERA POV
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
                }

                // update the global state with the current camera coordinates
                appViewModel.updateCameraCentroidCoordinates(event.cameraCoordinate)
            }
        }
    }

    fun onPermissionsChanged(permissionGranted: Boolean) {
        appViewModel.onLocationPermissionsChanged(permissionGranted)
    }

    // https://developer.android.com/training/location/receive-location-updates
    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            restaurantDealsRepository: RestaurantDealsRepository,
            dealMapper: RestaurantDealMapper,
            appViewModel: AppViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(
                    restaurantDealsRepository,
                    dealMapper,
                    appViewModel,
                ) as T
            }
        }
    }
}
