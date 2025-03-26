package com.example.grub.ui.dealImage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the DealImage route.
 */
data class DealImageUiState(
    val imageURL: String? = "",
)

/**
 * ViewModel that handles the business logic of the DealImage screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class DealImageViewModel(
    dealURL: String?,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(DealImageUiState())

    // UI state exposed to the UI
    var uiState: StateFlow<DealImageUiState> = viewModelState.asStateFlow()

    init {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    imageURL = dealURL,
                )
            }
        }
    }


    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            dealURL: String?,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DealImageViewModel(dealURL) as T
            }
        }
    }
}
