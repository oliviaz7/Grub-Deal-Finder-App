package com.example.grub.ui.addDealFlow

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.Result
import com.example.grub.data.StorageService
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
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
 * This is derived from [AddDealViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
data class AddDealUiState(
    val deals: List<RestaurantDeal>,
    val restaurants: List<RestaurantDeal>,
    val step: Step,
    val selectedRestaurant: SimpleRestaurant,
    val coordinates: LatLng = LatLng(43.4712, -80.5440),
)

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class AddDealViewModelState(
    val deals: List<RestaurantDeal>,
    val restaurants: List<RestaurantDeal>,
    val step: Step = Step.StepOne,
    val selectedRestaurant: SimpleRestaurant = SimpleRestaurant("", LatLng(0.0, 0.0), "")
) {

    /**
     * Converts this [AddDealViewModelState] into a more strongly typed [AddDealUiState] for driving
     * the ui.
     */
    fun toUiState(): AddDealUiState = AddDealUiState(deals, restaurants, step, selectedRestaurant)
}


/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class AddDealViewModel(
    private val dealsRepository: RestaurantDealsRepository,
    private val dealMapper: RestaurantDealMapper,
    private val storageService: StorageService,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(
        AddDealViewModelState(
            deals = emptyList(),
            restaurants = emptyList(),
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map(AddDealViewModelState::toUiState)
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
            dealsRepository.searchNearbyRestaurants("", LatLng(43.4712, -80.5440)).let { result -> // TODO: REPLACE LATLNG WITH CURR LOCATION VALUES
                when (result) {
                    is Result.Success -> {
                        val restaurants = result.data.map(dealMapper::mapResponseToRestaurantDeals)

                        Log.d("NEARBY OPTIONS", restaurants.toString())
                        viewModelState.update { it.copy(restaurants = restaurants) }
                    }
                    else -> Log.e("FetchingError", "SelectRestaurantViewModel, initial request failed")
                }
            }
        }
    }

    fun addNewRestaurantDeal(deal: RestaurantDealsResponse) {
        viewModelScope.launch {
            when (val result = dealsRepository.addRestaurantDeal(deal)) {
                is Result.Success -> {
                    // Handle success, e.g., update UI state or notify user
                    Log.d("AddDeal", "Deal added successfully")
                }
                is Result.Error -> {
                    // Handle error, e.g., show error message
                    Log.e("AddDeal", "Error adding deal: ${result.exception}")
                }
            }
        }
    }

    fun searchNearbyRestaurants(keyword: String, coordinates: LatLng, radius: Double) {
        viewModelScope.launch {
            dealsRepository.searchNearbyRestaurants(keyword, coordinates, radius).let { result ->
                when (result) {
                    is Result.Success -> {
                        val restaurants = result.data.map(dealMapper::mapResponseToRestaurantDeals)
                        viewModelState.update { it.copy(restaurants = restaurants) }
                        Log.d("searchNearbyRestaurants", "Search request successful")
                    }
                    else -> Log.e("FetchingError", "SelectRestaurantViewModel, search request failed")
                }
            }
        }
    }

    fun nextStep() {
        if (uiState.value.step == Step.StepOne) {
            viewModelState.update { it.copy(step = Step.StepTwo) }
        }
    }

    fun prevStep() {
        if (uiState.value.step == Step.StepTwo) {
            viewModelState.update { it.copy(step = Step.StepOne) }
        }
    }

    fun updateRestaurant(simpleRestaurant: SimpleRestaurant) {
        viewModelState.update { it.copy(selectedRestaurant = simpleRestaurant) }
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
                return AddDealViewModel(dealsRepository, dealMapper, storageService,) as T
            }
        }
    }
}