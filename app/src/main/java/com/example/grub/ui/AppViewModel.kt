package com.example.grub.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.auth.AuthRepository
import com.example.grub.model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Serves as a centralized UI state holder for app-wide data (eg. isLoggedIn)
 * Provides state flows for other screens to observe
 *
 * Is the single source of truth for authentication data, functions, and user profile details
 */
class AppViewModel(
    private val authRepository: AuthRepository,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : ViewModel() {

    // Current logged-in user (null if not logged in)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _currentUserLocation = MutableStateFlow<LatLng?>(null)
    val currentUserLocation: StateFlow<LatLng?> = _currentUserLocation.asStateFlow()
    
    // DO NOT EVER COLLECT/SUBSCRIBE THIS STATE (it updates so frequently our recompositions will be crazy)
    // ONLY USE IT TO GET THE .VALUE PROPERTY EXACTLY WHEN YOU NEED IT
    private val _mapCameraCentroidCoordinates = MutableStateFlow<LatLng?>(null)
    val mapCameraCentroidCoordinates: StateFlow<LatLng?> = _mapCameraCentroidCoordinates.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.loggedInUser.collect { user ->
                _currentUser.value = user
            }
        }

        getCurrentUserLocation()
    }

    /**
     * Update our own state when we detect a change in location permissions
     */
    fun onPermissionsChanged(permissionGranted: Boolean) {
        Log.i("location-permission", "location granted: $permissionGranted")
        _hasLocationPermission.value = permissionGranted

        if (permissionGranted) {
            getCurrentUserLocation()
        }
    }

    /**
     * Fetches the last known location and updates the UI state.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentUserLocation() {
        if (!_hasLocationPermission.value) {
            Log.e("user-location", "Permission denied, cannot fetch location")
            return
        }

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    _currentUserLocation.value = latLng
                    Log.d("user-location", "Current user location: $latLng")
                } ?: Log.e("user-location", "Failed to get current location")
            }
    }
    
    fun updateCameraCentroidCoordinates(centroid: LatLng?) {
        _mapCameraCentroidCoordinates.value = centroid
    }
    
    /**
     * Factory for AppViewModel that takes AuthRepository as a dependency
     */
    companion object {
        fun provideFactory(
            authRepository: AuthRepository,
            fusedLocationProviderClient: FusedLocationProviderClient,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AppViewModel(authRepository, fusedLocationProviderClient) as T
            }
        }
    }
}