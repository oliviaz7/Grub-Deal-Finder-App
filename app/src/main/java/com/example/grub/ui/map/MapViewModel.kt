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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.Priority

/**
 * UI state for the Map route.
 */
data class MapUiState(
    val restaurantDeals: List<RestaurantDeal>,
    val hasLocationPermission: Boolean,
    val userLocation: LatLng?
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
            userLocation = null // default to null
        )
    )

    // UI state exposed to the UI (not mutable for safety)
    val uiState = _uiState.asStateFlow()

    private var locationCallback: LocationCallback? = null

    init {
        viewModelScope.launch {
//            restaurantDealsRepository.getRestaurantDeals(LatLng(0.0,0.0)).let { result -> // TODO: REPLACE LATLNG WITH CURR LOCATION VALUES
              restaurantDealsRepository.getRestaurantDeals(LatLng(37.4210983, -122.084), 10.0).let { result -> // TODO: REPLACE LATLNG WITH CURR LOCATION VALUES
                when (result) {
                    is Result.Success -> {
                        val deals = result.data.map(dealMapper::mapResponseToRestaurantDeals)
                        _uiState.update { it.copy(restaurantDeals = deals) }
                    }
                    else -> Log.e("FetchingError", "MapViewModel, initial request failed")
                }
            }
        }
    }

    /**
     * Update our own state when we detect a change in location permissions
     */
    fun onPermissionsChanged(hasLocationPermission: Boolean) {
        Log.d("location-permission", "location granted: $hasLocationPermission")
        _uiState.update {
            it.copy(hasLocationPermission = hasLocationPermission)
        }

        if (hasLocationPermission) {
            Log.d("user-location", "set up init location")
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

    // okay how to do live location updates?
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
