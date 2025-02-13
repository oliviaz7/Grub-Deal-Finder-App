package com.example.grub.ui.map

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

/**
 * UI state for the Map route.
 */
data class MapUiState(
    val restaurantDeals: List<RestaurantDeal>,
    val hasLocationPermission: Boolean,
)

/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class MapViewModel(
    private val restaurantDealsRepository: RestaurantDealsRepository,
    private val dealMapper: RestaurantDealMapper,
) : ViewModel() {

    // Mutable state that view model operations should update
    private val _uiState = MutableStateFlow(
        MapUiState(
            restaurantDeals = emptyList(),
            hasLocationPermission = false,
        )
    )

    // UI state exposed to the UI (not mutable for safety)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            restaurantDealsRepository.getRestaurantDeals().let { result ->
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
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            restaurantDealsRepository: RestaurantDealsRepository,
            dealMapper: RestaurantDealMapper,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(restaurantDealsRepository, dealMapper) as T
            }
        }
    }
}
