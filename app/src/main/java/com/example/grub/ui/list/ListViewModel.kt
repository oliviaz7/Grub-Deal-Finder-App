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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.deals.DealsRepository
import com.example.grub.data.interests.InterestSection
import com.example.grub.data.interests.InterestsRepository
import com.example.grub.data.interests.TopicSelection
import com.example.grub.data.successOr
import com.example.grub.model.Deal
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Interests screen
 */
data class ListUiState(
    val deals: List<Deal> = emptyList(),
    val loading: Boolean = false,
)

class ListViewModel(
    private val dealsRepository: DealsRepository
) : ViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(ListUiState(loading = true))
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()


    init {
        refreshAll()
    }


    /**
     * Refresh topics, people, and publications
     */
    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            // Trigger repository requests in parallel
            val dealsDeferred = async { dealsRepository.getDeals() }

            // Wait for all requests to finish
            val deals = dealsDeferred.await().successOr(emptyList())

            _uiState.update {
                it.copy(
                    loading = false,
                    deals = deals
                )
            }
        }
    }

    /**
     * Factory for ListViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            dealsRepository: DealsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ListViewModel(dealsRepository) as T
            }
        }
    }
}
