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
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.Result
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.model.DealType
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.mappers.RestaurantDealMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng

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
    val searchText: String = ""
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
            restaurantDealsRepository.getRestaurantDeals(LatLng(43.5315 ,-79.6131)).let { result -> // TODO: REPLACE LATLNG WITH VALID CURR LOCATION VALUES
                when (result) {
                    is Result.Success -> {
                        val deals = result.data.map(dealMapper::mapResponseToRestaurantDeals)
                        viewModelState.update {
                            it.copy(
                                restaurantDeals = deals,
                                filteredDeals = deals
                            )
                        }
                    }

                    else -> Log.e("FetchingError", "ListViewModel, initial request failed")
                }
            }
        }
    }

    fun onSearchTextChange(newText: String) {
        viewModelState.update { currentState ->
            currentState.copy(searchText = newText)
        }
    }

    fun onFilterSelected(filter: String) {
        viewModelState.update { it.copy(selectedFilter = filter) }
        viewModelState.update {
            it.copy(
                filteredDeals = filterDeals(
                    viewModelState.value.restaurantDeals,
                    filter
                )
            )
        }
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
        viewModelState.update {
            it.copy(
                filteredDeals = filterCustomDeals(
                    viewModelState.value.restaurantDeals
                )
            )
        }
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
