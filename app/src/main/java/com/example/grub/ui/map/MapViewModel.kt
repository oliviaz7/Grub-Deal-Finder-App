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

package com.example.grub.ui.map

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.Result
import com.example.grub.data.deals.DealsRepository
import com.example.grub.model.Deal
import com.example.grub.model.mappers.DealMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Map route.
 *
 * This is derived from [MapViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
data class MapUiState(
    val deals: List<Deal>
)

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class MapViewModelState(
    val deals: List<Deal>
) {

    /**
     * Converts this [MapViewModelState] into a more strongly typed [MapUiState] for driving
     * the ui.
     */
    fun toUiState(): MapUiState = MapUiState(deals)
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class MapViewModel(
    private val dealsRepository: DealsRepository,
    private val dealMapper: DealMapper,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        MapViewModelState(
            deals = emptyList()
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map(MapViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            dealsRepository.getDeals().let { result ->
                when (result) {
                    is Result.Success -> {
                        val deals = result.data.map(dealMapper::mapRawDealToDeal)
                        viewModelState.update { it.copy(deals = deals) }
                    }
                    else -> Log.e("FetchingError", "MapViewModel, initial request failed")
                }
            }
        }
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            dealsRepository: DealsRepository,
            dealMapper: DealMapper,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(dealsRepository, dealMapper) as T
            }
        }
    }
}
