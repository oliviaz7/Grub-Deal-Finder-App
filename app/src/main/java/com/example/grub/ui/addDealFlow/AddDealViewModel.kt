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
import com.example.grub.data.deals.AddDealResponse
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.data.deals.SimpleRestaurant
import com.example.grub.model.ApplicableGroup
import com.example.grub.model.DealType
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.mappers.RestaurantDealMapper
import com.example.grub.ui.AppViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
    val restaurantSearchText: String,
    val imageUri: Uri?,
    val addDealResult: Result<AddDealResponse>?,
    val userId: String,
    val dealState: DealState,
)

data class DealState(
    val itemName: String = "",
    val description: String? = null,
    val price: String? = null,
    val dealType: DealType? = null,
    val expiryDate: String? = null,
    val startTimes: List<Int> = List(7){0}, // array of 7 integers, each representing a day of the week
    val endTimes: List<Int> = List(7){24 * 60}, // integers representing the end time for each day of the week, in the range [0, 24]
    val restrictions: String? = null,
    val applicableGroup: ApplicableGroup = ApplicableGroup.ALL // set of applicable groups
)

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class AddDealViewModelState(
    val deals: List<RestaurantDeal>,
    val restaurants: List<RestaurantDeal>,
    val step: Step = Step.Step1,
    val selectedRestaurant: SimpleRestaurant = SimpleRestaurant("", LatLng(0.0, 0.0), ""),
    val restaurantSearchText: String = "",
    val imageUri: Uri? = null,
    val userId: String = "",
    val addDealResult: Result<AddDealResponse>? = null,
    val dealState: DealState = DealState(),
) {

    /**
     * Converts this [AddDealViewModelState] into a more strongly typed [AddDealUiState] for driving
     * the ui.
     */
    fun toUiState(): AddDealUiState =
        AddDealUiState(deals, restaurants, step, selectedRestaurant, restaurantSearchText, imageUri, addDealResult, userId, dealState)
}


/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class AddDealViewModel(
    private val dealsRepository: RestaurantDealsRepository,
    private val dealMapper: RestaurantDealMapper,
    private val storageService: StorageService,
    private val appViewModel: AppViewModel,
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
        Log.d("uploadImage", "UPLOADING IMAGE: ${androidUri}")
        storageService.uploadDealImage(
            dealId = "deal_" + System.currentTimeMillis(), // TODO: come up with a better ID
            fileUri = androidUri,
            onSuccess = { uploadedUrl: String -> println("UPLOAD SUCCESS: $uploadedUrl") },
            onFailure = { println("UPLOAD FAILED") },
        )
    }

    init {
        println("ex. how to get the logged in user: ${appViewModel.currentUser.value}")
        viewModelScope.launch {
            if (appViewModel.currentUser.value?.id == null) {
                Log.e("Add Deal Launch", "No user id")
                return@launch
            } else {
                viewModelState.update { it.copy(userId = appViewModel.currentUser.value!!.id) }
            }
            searchNearbyRestaurants("", 1000.0)
        }
    }

    fun addNewRestaurantDeal(deal: RestaurantDealsResponse) {
        viewModelScope.launch {
            Log.d("AddDeal", "Adding deal: $deal")
            delay(2000) // Wait for 2 seconds, TODO: remove this
            val result = dealsRepository.addRestaurantDeal(deal)
            viewModelState.update { it.copy(addDealResult = result) }
            when (result) {
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

    fun searchNearbyRestaurants(keyword: String, radius: Double) {
        viewModelScope.launch {
            val coordinates = appViewModel.currentUserLocation.value
            if (coordinates == null) {
                Log.e("searchNearbyRestaurants", "No user location available")
                return@launch
            }
            dealsRepository.searchNearbyRestaurants(keyword, coordinates!!, radius).let { result ->
                when (result) {
                    is Result.Success -> {
                        val restaurants = result.data.map(dealMapper::mapResponseToRestaurantDeals)
                        viewModelState.update { it.copy(restaurants = restaurants) }
                        Log.d("searchNearbyRestaurants", "Search request successful")
                    }

                    else -> Log.e(
                        "FetchingError",
                        "SelectRestaurantViewModel, search request failed"
                    )
                }
            }
        }
    }

    fun nextStep() {
        viewModelState.update {
            it.copy(step = uiState.value.step.nextStep())
        }
    }

    fun prevStep() {
        viewModelState.update {
            it.copy(step = uiState.value.step.prevStep())
        }
    }

    fun updateRestaurant(simpleRestaurant: SimpleRestaurant) {
        viewModelState.update { it.copy(selectedRestaurant = simpleRestaurant) }
    }

    fun onSearchTextChange(searchText: String) {
        viewModelState.update { it.copy(restaurantSearchText = searchText) }
    }

    fun updateImageUri(uri: Uri?) {
        viewModelState.update { it.copy(imageUri = uri) }
    }

    fun updateItemName(itemName: String) {
        viewModelState.update { it.copy(dealState = it.dealState.copy(itemName = itemName)) }
    }

    fun updateDescription(description: String?) {
        viewModelState.update { it.copy(dealState = it.dealState.copy(description = description)) }
    }

    fun updatePrice(price: String?) {
        viewModelState.update { it.copy(dealState = it.dealState.copy(price = price)) }
    }

    fun updateDealType(dealType: DealType) {
        viewModelState.update { it.copy(dealState = it.dealState.copy(dealType = dealType)) }
    }

    fun updateExpiryDate(expirySelectedDate: String?) {
        viewModelState.update { it.copy(dealState = it.dealState.copy(expiryDate = expirySelectedDate)) }
    }

    fun updateStartTimes(startTimes: List<Int>) {
        if (startTimes.isEmpty()) { // reset to default - available every day 0-24
            viewModelState.update { it.copy(dealState = it.dealState.copy(startTimes = List(7){0})) }
        } else {
            viewModelState.update { it.copy(dealState = it.dealState.copy(startTimes = startTimes)) }
        }
    }

    fun updateEndTimes(endTimes: List<Int>) {
        if (endTimes.isEmpty()) { // reset to default - available every day 0-24
            viewModelState.update { it.copy(dealState = it.dealState.copy(endTimes = List(7){24 * 60})) }
        } else {
            viewModelState.update { it.copy(dealState = it.dealState.copy(endTimes = endTimes)) }
        }

    }

    fun updateApplicableGroup(applicableGroup: ApplicableGroup) {
        viewModelState.update { currentState ->
            currentState.copy(dealState = currentState.dealState.copy(applicableGroup = applicableGroup))
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
            appViewModel: AppViewModel,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddDealViewModel(
                    dealsRepository,
                    dealMapper,
                    storageService,
                    appViewModel
                ) as T
            }
        }
    }
}