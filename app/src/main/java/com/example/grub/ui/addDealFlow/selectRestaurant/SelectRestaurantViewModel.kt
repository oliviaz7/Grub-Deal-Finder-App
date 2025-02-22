package com.example.grub.ui.addDealFlow.selectRestaurant

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.Result
import com.example.grub.data.StorageService
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.mappers.RestaurantDealMapper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the SelectRestaurant route.
 *
 * This is derived from [SelectRestaurantViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
data class SelectRestaurantUiState(
    val deals: List<RestaurantDeal>
)

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class SelectRestaurantViewModelState(
    val deals: List<RestaurantDeal>
) {

    /**
     * Converts this [SelectRestaurantViewModelState] into a more strongly typed [SelectRestaurantUiState] for driving
     * the ui.
     */
    fun toUiState(): SelectRestaurantUiState = SelectRestaurantUiState(deals)
}


/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class SelectRestaurantViewModel(
    private val dealsRepository: RestaurantDealsRepository,
    private val dealMapper: RestaurantDealMapper,
    private val storageService: StorageService,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(
        SelectRestaurantViewModelState(
            deals = emptyList()
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map(SelectRestaurantViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    val uploadImage = { androidUri: Uri ->
        storageService.uploadDealImage(
            dealId = "deal_" + System.currentTimeMillis(), // TODO: come up with a better ID
            fileUri = androidUri,
            onSuccess = { uploadedUrl: String -> println("UPLOAD SUCCESS: $uploadedUrl") },
            onFailure = { println("UPLOAD FAILED") },
        )
    }

    init {
        viewModelScope.launch {
            dealsRepository.getRestaurantDeals(LatLng(0.0, 0.0)).let { result -> // TODO: REPLACE LATLNG WITH CURR LOCATION VALUES
                when (result) {
                    is Result.Success -> {
                        val deals = result.data.map(dealMapper::mapResponseToRestaurantDeals)
                        viewModelState.update { it.copy(deals = deals) }
                    }
                    else -> Log.e("FetchingError", "SelectRestaurantViewModel, initial request failed")
                }
            }
        }
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            dealsRepository: RestaurantDealsRepository,
            dealMapper: RestaurantDealMapper,
            storageService: StorageService,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SelectRestaurantViewModel(dealsRepository, dealMapper, storageService,) as T
            }
        }
    }
}