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

package com.example.grub.ui.dealDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.model.Deal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Map route.
 *
 * This is derived from [DealDetailViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
data class DealDetailUiState(
    val deal: Deal?
)

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class DealDetailViewModelState(
    val deal: Deal?
) {

    /**
     * Converts this [DealDetailViewModelState] into a more strongly typed [DealDetailUiState] for driving
     * the ui.
     */
    fun toUiState(): DealDetailUiState = DealDetailUiState(deal)
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class DealDetailViewModel(
    private val deal: Deal
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        DealDetailViewModelState(
            deal = null
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map(DealDetailViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            // Deserialize the JSON string back to the Deal object
            viewModelState.update { it.copy(deal = deal) }
        }
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            deal: Deal
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DealDetailViewModel(deal) as T
            }
        }
    }
}