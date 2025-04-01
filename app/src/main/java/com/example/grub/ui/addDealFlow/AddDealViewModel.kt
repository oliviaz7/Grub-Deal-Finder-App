package com.example.grub.ui.addDealFlow

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.Result
import com.example.grub.data.StorageService
import com.example.grub.data.deals.AddDealResponse
import com.example.grub.data.deals.AutoPopulateDealsResponse
import com.example.grub.data.deals.RawDeal
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.RestaurantDealsResponse
import com.example.grub.model.ApplicableGroup
import com.example.grub.model.DealType
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.mappers.ApplicableGroupsMapper
import com.example.grub.model.mappers.DealTypeMapper
import com.example.grub.model.mappers.MAX_MINUTES_IN_DAY
import com.example.grub.model.mappers.MIN_MINUTES_IN_DAY
import com.example.grub.model.mappers.RestaurantDealMapper
import com.example.grub.service.DealImageRequestBody
import com.example.grub.service.RetrofitGpuClient.gpuApiService
import com.example.grub.ui.AppViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

/**
 * UI state for the SelectRestaurant route.
 *
 * This is derived from [AddDealViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
data class AddDealUiState(
    val deals: List<RestaurantDeal>,
    val restaurants: List<RestaurantDeal>?, // null means we haven't fetched the data yet, [] means no results
    val step: Step,
    val selectedRestaurant: RestaurantDeal,
    val restaurantSearchText: String,
    val imageUri: Uri?,
    val addDealResult: Result<AddDealResponse>?,
    val restaurantDealLoading: Boolean,
    val userId: String,
    val dealState: DealState,
    val autoPopulateLoading: Boolean,
)

data class DealState(
    val itemName: String = "",
    val description: String? = null,
    val price: String? = null,
    val dealType: DealType? = DealType.DISCOUNT,
    val imageKey: String? = null,
    val expiryDate: ZonedDateTime? = null,
    val startTimes: List<Int> = List(7) { MIN_MINUTES_IN_DAY }, // array of 7 integers, each representing a day of the week
    val endTimes: List<Int> = List(7) { MAX_MINUTES_IN_DAY }, // integers representing the end time for each day of the week, in the range [0, 24]
    val restrictions: String? = null,
    val applicableGroup: ApplicableGroup = ApplicableGroup.ALL // set of applicable groups
)

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class AddDealViewModelState(
    val deals: List<RestaurantDeal>,
    val restaurants: List<RestaurantDeal>?,
    val step: Step = Step.Step1,
    val selectedRestaurant: RestaurantDeal = RestaurantDeal(
        id = "",
        placeId = "",
        restaurantName = "",
        coordinates = LatLng(0.0, 0.0),
        deals = emptyList(),
        displayAddress = null,
        imageUrl = null
    ),
    val restaurantSearchText: String = "",
    val imageUri: Uri? = null,
    val userId: String = "",
    val addDealResult: Result<AddDealResponse>? = null,
    val restaurantDealLoading: Boolean = false,
    val dealState: DealState = DealState(),
    val autoPopulateLoading: Boolean = false,
    val isGpuOnline: Boolean = false,
) {

    /**
     * Converts this [AddDealViewModelState] into a more strongly typed [AddDealUiState] for driving
     * the ui.
     */
    fun toUiState(): AddDealUiState =
        AddDealUiState(
            deals,
            restaurants,
            step,
            selectedRestaurant,
            restaurantSearchText,
            imageUri,
            addDealResult,
            restaurantDealLoading,
            userId,
            dealState,
            autoPopulateLoading,
        )
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

    private var _prevSearchRestaurantsKeyword = mutableStateOf<String>("previous")

    // this is the unique id/key/name of the image in the firebase storage bucket
    // this is what we will store in the database, and is used to reconstruct the full URL
    // to display the image later
    private fun updateImageKey(imageId: String) {
        viewModelState.update { it.copy(dealState = it.dealState.copy(imageKey = imageId)) }
    }

    fun uploadImageToFirebase(androidUri: Uri) {
        Log.d("uploadImage", "UPLOADING IMAGE: $androidUri")
        val imageKey = "deal_${viewModelState.value.userId}_${System.currentTimeMillis()}"

        storageService.uploadDealImage(
            dealId = imageKey,
            fileUri = androidUri,
            onSuccess = { uploadedUrl: String ->
                Log.d("uploadImage", "UPLOAD SUCCESS $uploadedUrl")
                updateImageKey(imageKey)
                if (viewModelState.value.isGpuOnline) {
                    Log.d("auto-populate-deal", "GPU is online")
                    autoPopulateDealFromImage()
                } else {
                    Log.d("auto-populate-deal", "GPU is NOT online")
                }
            },
            onFailure = {
                Log.e("uploadImage", "UPLOAD FAILED RIP")
            },
        )
    }

    // TODO: change this from being hardcoded to making a request to the python server
    //  and finding out if it's online
    private fun isGpuOnlineCheck() {
        // something like
        // return apiService.gpuHandshake()

        // delete this:
        appViewModel.onGpuOnlineChange(true)
    }

    init {
        viewModelScope.launch {
            if (appViewModel.currentUser.value?.id == null) {
                Log.e("Add Deal Launch", "No user id")
                return@launch
            } else {
                viewModelState.update { it.copy(userId = appViewModel.currentUser.value!!.id) }
            }
            searchNearbyRestaurants("", 1000.0)
        }

        // listen for the state of GPU server
        viewModelScope.launch {
            appViewModel.isGpuOnline.collect { isOnline ->
                viewModelState.update { it.copy(isGpuOnline = isOnline) }
            }
        }

        // trigger check on initial load
        isGpuOnlineCheck()
    }

    fun addNewRestaurantDeal() {
        viewModelScope.launch {
            val restaurantDeal = getRestaurantDealsResponse()
            Log.d("AddDeal", "Adding deal: $restaurantDeal")
            val result = dealsRepository.addRestaurantDeal(restaurantDeal)
            viewModelState.update { it.copy(addDealResult = result) } // let the UI know the result
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

    private fun updateAutoPopulateDealFields(response: AutoPopulateDealsResponse) {
        val currentDealState = viewModelState.value.dealState

        val newItemName =
            response.itemName.takeIf { it != "null" } ?: currentDealState.itemName
        val newDescription =
            response.dealDescription.takeIf { it != "null" } ?: currentDealState.description
        val newPrice = response.price.takeIf { it != "null" } ?: currentDealState.price
        val newDealType =
            response.dealType.takeIf { it != "null" }?.let { DealTypeMapper.mapToDealType(it) }
                ?: currentDealState.dealType
        val newApplicableGroup = response.applicableGroup.takeIf { it != "null" }
            ?.let { ApplicableGroupsMapper.mapToApplicableGroups(it) }
            ?: currentDealState.applicableGroup

        viewModelState.update {
            it.copy(
                dealState = currentDealState.copy(
                    itemName = newItemName,
                    description = newDescription,
                    price = newPrice,
                    dealType = newDealType,
                    applicableGroup = newApplicableGroup
                )
            )
        }
    }

    // must guarantee that image key is available by this time
    // the only way we can currently guarantee this is by calling this function
    // on the success after the image has been uploaded
    private fun autoPopulateDealFromImage() {
        viewModelScope.launch {
            try {
                Log.d(
                    "auto_populate_deal_from_image",
                    "imageKey: ${viewModelState.value.dealState.imageKey}"
                )
                // Ensure imageId is available
                val imageId = viewModelState.value.dealState.imageKey ?: return@launch
                // TODO: ADD ONLY IF THE GPU SERVER IS ONLINE
                viewModelState.update { it.copy(autoPopulateLoading = true) }
                val response =
                    gpuApiService.autoPopulateDealFromImage(DealImageRequestBody(imageId))
                // SET LOADING TO BE TRUE
                viewModelState.update { it.copy(autoPopulateLoading = false) }

                updateAutoPopulateDealFields(response)
                // Handle response, e.g., update uiState with result
                Log.d("auto_populate_deal_from_image", "response: $response")
            } catch (e: Exception) {
                // SET LOADING TO BE FALSE
                // Handle error (e.g., network failure)
                viewModelState.update { it.copy(autoPopulateLoading = false) }
                Log.d("auto_populate_deal_from_image", "error: $e")
            }
        }
    }

    private fun getRestaurantDealsResponse(): RestaurantDealsResponse {
        return RestaurantDealsResponse(
            id = "default_id",
            placeId = uiState.value.selectedRestaurant.placeId,
            coordinates = uiState.value.selectedRestaurant.coordinates,
            restaurantName = uiState.value.selectedRestaurant.restaurantName,
            displayAddress = uiState.value.selectedRestaurant.displayAddress ?: "",
            rawDeals = listOf(
                RawDeal(
                    // TODO: add price to raw deal obj
                    id = "default_deal_id", // will be added in the BE
                    item = uiState.value.dealState.itemName,
                    description = uiState.value.dealState.description,
                    type = uiState.value.dealState.dealType!!,
                    expiryDate = uiState.value.dealState.expiryDate?.toInstant()?.toEpochMilli(),
                    datePosted = System.currentTimeMillis(),
                    userId = uiState.value.userId,
                    // we could've uploaded an image to the server and gotten a URL back,
                    // but then removed it (nulling our the imageUri) without wiping the imageKey
                    imageId = if (uiState.value.imageUri != null) uiState.value.dealState.imageKey else null,
                    applicableGroup = uiState.value.dealState.applicableGroup,
                    dailyStartTimes = uiState.value.dealState.startTimes,
                    dailyEndTimes = uiState.value.dealState.endTimes,
                )
            )
        )
    }

    fun searchNearbyRestaurants(keyword: String, radius: Double) {
        viewModelScope.launch {
            val coordinates = appViewModel.mapCameraCentroidCoordinates.value
            if (coordinates == null) {
                Log.e("searchNearbyRestaurants", "No location available")
                return@launch
            }
            if (keyword == _prevSearchRestaurantsKeyword.value) {
                Log.d("searchNearbyRestaurants", "No need to search again")
                return@launch
            } else {
                _prevSearchRestaurantsKeyword.value = keyword
            }
            // clear the previous search results
            viewModelState.update { it.copy(restaurants = null) }
            // search for nearby restaurants
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
        viewModelState.update { it.copy(restaurantDealLoading = true) } // show loading bar on next page
    }

    fun getRestaurantDeals() {
        viewModelScope.launch {
            val placeId = uiState.value.selectedRestaurant.placeId
            if (placeId.isEmpty()) {
                Log.e("getRestaurantDeals", "No placeId available")
                return@launch
            }

            Log.d("getRestaurantDeals", "Searching for restaurant deals: $placeId")
            try {
                val result =
                    dealsRepository.getRestaurant(placeId)  // Assuming getRestaurant() returns Result
                viewModelState.update { it.copy(restaurantDealLoading = true) } // show loading bar
                when (result) {
                    is Result.Success -> {

                        if (result.data.restaurant == null || result.data.restaurant.rawDeals.isEmpty()) {
                            Log.d(
                                "getRestaurantDeals",
                                "No restaurant deal data available in the response"
                            )
                            nextStep(Step.Step3) // skip the show existing deals step
                        } else {
                            val mappedRestaurantDeal =
                                dealMapper.mapResponseToRestaurantDeals(result.data.restaurant)
                            viewModelState.update { it.copy(selectedRestaurant = mappedRestaurantDeal) } // copy to selected restaurant
                            Log.d(
                                "ShowExistingDeals",
                                "Successfully retrieved restaurant: ${mappedRestaurantDeal}"
                            )
                        }
                    }

                    is Result.Error -> {
                        Log.e(
                            "getRestaurantDeals",
                            "Error retrieving restaurant deals: ${result.exception.localizedMessage}"
                        )
                        nextStep(Step.Step3) // skip the show existing deals step
                    }
                }
            } catch (e: Exception) {
                Log.e("getRestaurantDeals", "Exception occurred: ${e.localizedMessage}")
            }
            viewModelState.update { it.copy(restaurantDealLoading = false) }
        }
    }

    fun nextStep(step: Step? = null) {
        viewModelState.update {
            if (step == null) {
                it.copy(step = uiState.value.step.nextStep())
            } else {
                it.copy(step = step)
            }
        }
    }

    fun prevStep(step: Step? = null) {
        viewModelState.update {
            if (step == null) {
                it.copy(step = uiState.value.step.prevStep())
            } else {
                it.copy(step = step)
            }
        }
    }

    fun updateRestaurant(restaurant: RestaurantDeal) {
        viewModelState.update { it.copy(selectedRestaurant = restaurant) }
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

    fun updateExpiryDate(expiryDate: ZonedDateTime) {
        viewModelState.update { it.copy(dealState = it.dealState.copy(expiryDate = expiryDate)) }
    }

    fun updateStartTimes(startTimes: List<Int>) {
        if (startTimes.isEmpty()) { // reset to default - available every day 0-24
            viewModelState.update { it.copy(dealState = it.dealState.copy(startTimes = List(7) { MIN_MINUTES_IN_DAY })) }
        } else {
            viewModelState.update { it.copy(dealState = it.dealState.copy(startTimes = startTimes)) }
        }
    }

    fun updateEndTimes(endTimes: List<Int>) {
        if (endTimes.isEmpty()) { // reset to default - available every day 0-24
            viewModelState.update { it.copy(dealState = it.dealState.copy(endTimes = List(7) { MAX_MINUTES_IN_DAY })) }
        } else {
            viewModelState.update { it.copy(dealState = it.dealState.copy(endTimes = endTimes)) }
        }

    }

    fun updateApplicableGroup(applicableGroup: ApplicableGroup) {
        viewModelState.update { currentState ->
            currentState.copy(dealState = currentState.dealState.copy(applicableGroup = applicableGroup))
        }
    }

    fun onCameraPermissionsChanged(permissionGranted: Boolean) {
        appViewModel.onCameraPermissionsChange(permissionGranted)
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