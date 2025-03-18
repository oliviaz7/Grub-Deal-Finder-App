package com.example.grub.ui.list

import CustomFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.model.ApplicableGroup
import com.example.grub.model.DayOfWeekAndTimeRestriction
import com.example.grub.model.DealType
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.mappers.RestaurantDealMapper
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * UI state for the List route.
 */
data class ListUiState(
    val restaurantDeals: List<RestaurantDeal> = emptyList(),
    val filteredDeals: List<RestaurantDeal> = emptyList(),
    val loading: Boolean = false,
    val selectedFilter: String = "All",
    val showFilterDialog: Boolean = false,
    val selectedCustomFilter: CustomFilter = CustomFilter(),
    val searchText: String = "",
    val selectedSort: String = "",
)

/**
 * ViewModel that handles the business logic of the List screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class ListViewModel(
    private val restaurantDealsRepository: RestaurantDealsRepository,
    private val appViewModel: AppViewModel,
    private val dealMapper: RestaurantDealMapper,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ListUiState())

    // UI state exposed to the UI
    var uiState: StateFlow<ListUiState> = viewModelState.asStateFlow()

    init {
        viewModelScope.launch {
            // List view model just needs to subscribe to accumulated deals,
            // don't need to worry about making the call to getRestaurantDeals
            // since map view model handles that
            restaurantDealsRepository.accumulatedDeals().collect { accumulatedDeals ->
                val mappedDeals =
                    accumulatedDeals.map { dealMapper.mapResponseToRestaurantDeals(it) }
                viewModelState.update {
                    it.copy(
                        restaurantDeals = mappedDeals,
                    )
                }
                onFilter()
            }
        }
    }

    fun onSearchTextChange(newText: String) {
        viewModelState.update { currentState ->
            currentState.copy(searchText = newText)
        }
    }

    fun onFilter() {
        val sortedDeals: List<RestaurantDeal> = when (uiState.value.selectedSort) {
            "Distance" -> sortByDistance(uiState.value.restaurantDeals)
            "Date Posted" -> uiState.value.restaurantDeals
                .map { it.copy(deals = it.deals.sortedByDescending { deal -> deal.datePosted }) }
                .sortedByDescending { it.deals.firstOrNull()?.datePosted }

            "Up Votes" -> uiState.value.restaurantDeals
                .map { it.copy(deals = it.deals.sortedByDescending { deal -> deal.numUpVotes - deal.numDownVotes }) }
                .sortedByDescending { it.deals.firstOrNull()?.numUpVotes }

            else -> uiState.value.restaurantDeals
        }
        viewModelState.update { currentState ->
            currentState.copy(
                restaurantDeals = sortedDeals,
            )
        }
        val query = uiState.value.searchText.trim()
        val searchFilteredList = if (query.isBlank()) {
            uiState.value.restaurantDeals
        } else {
            uiState.value.restaurantDeals.map { restaurantDeal ->
                val matchingDeals = restaurantDeal.deals.filter { deal ->
                    restaurantDeal.restaurantName.contains(query, ignoreCase = true) ||
                            deal.item.contains(query, ignoreCase = true)
                }
                restaurantDeal.copy(deals = matchingDeals)
            }.filter { it.deals.isNotEmpty() }
        }

        // Apply deal type filtering on search results
        val finalFilteredList = if (uiState.value.selectedFilter == "Custom")
            filterCustomDeals(searchFilteredList)
        else filterDeals(searchFilteredList, uiState.value.selectedFilter)

        // Update state with intersection of search and filter results
        viewModelState.update { currentState ->
            currentState.copy(filteredDeals = finalFilteredList)
        }
    }

    fun onFilterSelected(filter: String) {
        viewModelState.update { it.copy(selectedFilter = filter) }
        onFilter()
    }

    fun onSelectCustomFilter(category: String, filter: String) {
        viewModelState.update { currentState ->
            val updatedFilters = when (category) {
                "type" -> {
                    val currentTypeSet = currentState.selectedCustomFilter.type
                    val newTypeSet = if (filter.uppercase() in currentTypeSet) {
                        currentTypeSet - filter.uppercase()
                    } else {
                        currentTypeSet + filter.uppercase()
                    }
                    currentState.selectedCustomFilter.copy(type = newTypeSet)
                }

                "day" -> {
                    val currentDaySet = currentState.selectedCustomFilter.day
                    val newDaySet = if (filter in currentDaySet) {
                        currentDaySet - filter
                    } else {
                        currentDaySet + filter
                    }
                    currentState.selectedCustomFilter.copy(day = newDaySet)
                }

                "restrictions" -> {
                    val currentRestrictionsSet = currentState.selectedCustomFilter.restrictions
                    val newRestrictionsSet = if (filter in currentRestrictionsSet) {
                        currentRestrictionsSet - filter
                    } else {
                        currentRestrictionsSet + filter
                    }
                    currentState.selectedCustomFilter.copy(restrictions = newRestrictionsSet)
                }

                else -> currentState.selectedCustomFilter
            }

            currentState.copy(selectedCustomFilter = updatedFilters)
        }
    }

    fun onSubmitCustomFilter() {
        viewModelState.update { it.copy(selectedFilter = "Custom") }
        onFilter()
        onShowFilterDialog(false)
    }

    private fun filterCustomDeals(deals: List<RestaurantDeal>): List<RestaurantDeal> {
        println(viewModelState.value.selectedCustomFilter.type)
        return deals.map { restaurantDeal ->
            val filteredDeals = restaurantDeal.deals.filter { deal ->
                (viewModelState.value.selectedCustomFilter.type.isEmpty() || deal.type.name in viewModelState.value.selectedCustomFilter.type)
                        &&
                        (viewModelState.value.selectedCustomFilter.restrictions.isEmpty() ||
                                deal.applicableGroup.toString() in viewModelState.value.selectedCustomFilter.restrictions ||
                                deal.applicableGroup == ApplicableGroup.ALL ||
                                deal.applicableGroup == ApplicableGroup.NONE
                                )
                        &&
                        (viewModelState.value.selectedCustomFilter.day.isEmpty() || deal.activeDayTime.let { restriction ->
                            when (restriction) {
                                is DayOfWeekAndTimeRestriction.NoRestriction -> true
                                is DayOfWeekAndTimeRestriction.DayOfWeekRestriction ->
                                    restriction.activeDays.any {
                                        it.toString() in viewModelState.value.selectedCustomFilter.day
                                    }
                                is DayOfWeekAndTimeRestriction.BothDayAndTimeRestriction ->
                                    restriction.activeDaysAndTimes.any {
                                        it.dayOfWeek.toString() in viewModelState.value.selectedCustomFilter.day
                                    }
                            }
                        })
            }
            restaurantDeal.copy(deals = filteredDeals)
        }.filter { it.deals.isNotEmpty() }
    }

    private fun filterDeals(deals: List<RestaurantDeal>, filter: String): List<RestaurantDeal> {
        return deals.map { restaurantDeal ->
            val filteredDeals = when (filter) {
                "All" -> restaurantDeal.deals
                "BOGO" -> restaurantDeal.deals.filter { it.type == DealType.BOGO }
                "Discount" -> restaurantDeal.deals.filter { it.type == DealType.DISCOUNT }
                "Free" -> restaurantDeal.deals.filter { it.type == DealType.FREE }
                else -> restaurantDeal.deals
            }
            restaurantDeal.copy(deals = filteredDeals)
        }.filter { it.deals.isNotEmpty() }
    }


    fun onShowFilterDialog(bool: Boolean) {
        viewModelState.update { it.copy(showFilterDialog = bool) }
    }

    fun onSortOptionSelected(option: String) {
        viewModelState.update { it.copy(selectedSort = option) }
        onFilter()
    }

    private fun sortByDistance(deals: List<RestaurantDeal>): List<RestaurantDeal> {
        val cameraCoordinates = appViewModel.mapCameraCentroidCoordinates.value
        val cameraLat = cameraCoordinates?.latitude
        val cameraLng = cameraCoordinates?.longitude

        return if (cameraLat != null && cameraLng != null) {
            deals.sortedBy { restaurant ->
                calculateDistance(
                    cameraLat,
                    cameraLng,
                    restaurant.coordinates.latitude,
                    restaurant.coordinates.longitude
                )
            }
        } else {
            deals
        }
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371 // Radius of the Earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // Distance in km
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            restaurantDealsRepository: RestaurantDealsRepository,
            appViewModel: AppViewModel,
            dealMapper: RestaurantDealMapper,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ListViewModel(restaurantDealsRepository, appViewModel, dealMapper) as T
            }
        }
    }
}
