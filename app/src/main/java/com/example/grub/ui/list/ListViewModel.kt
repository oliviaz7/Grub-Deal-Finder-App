/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grub.ui.list

import CustomFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.model.DealType
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.mappers.RestaurantDealMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            "Distance" -> uiState.value.restaurantDeals //todo: sortByDistance(uiState.value.restaurantDeals)
            "Date Posted" -> uiState.value.restaurantDeals.sortedByDescending { it.deals.firstOrNull()?.datePosted }
            "Up Votes" -> uiState.value.restaurantDeals // todo: need upvotes from be first
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
                        currentTypeSet - filter.uppercase() // Remove filter if it already exists
                    } else {
                        currentTypeSet + filter.uppercase() // Add filter if it doesn't exist
                    }
                    currentState.selectedCustomFilter.copy(type = newTypeSet)
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
                viewModelState.value.selectedCustomFilter.type.isEmpty() ||
                        deal.type.name in viewModelState.value.selectedCustomFilter.type
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
                return ListViewModel(restaurantDealsRepository, dealMapper) as T
            }
        }
    }
}
