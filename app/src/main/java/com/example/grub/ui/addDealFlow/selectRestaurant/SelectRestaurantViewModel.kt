package com.example.grub.ui.addDealFlow.selectRestaurant

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
 * UI state for the SelectRestaurant route.
 *
 * This is derived from [SelectRestaurantViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
data class SelectRestaurantUiState(
    val deals: List<Deal>
)

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class SelectRestaurantViewModelState(
    val deals: List<Deal>
) {

    /**
     * Converts this [SelectRestaurantViewModelState] into a more strongly typed [SelectRestaurantUiState] for driving
     * the ui.
     */
    fun toUiState(): SelectRestaurantUiState = SelectRestaurantUiState(deals)
}


/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class SelectRestaurantViewModel(
    private val dealsRepository: DealsRepository,
    private val dealMapper: DealMapper,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(
        SelectRestaurantViewModelState(
            deals = emptyList()
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map(SelectRestaurantViewModelState::toUiState)
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
                    else -> Log.e("FetchingError", "SelectRestaurantViewModel, initial request failed")
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
                return SelectRestaurantViewModel(dealsRepository, dealMapper) as T
            }
        }
    }
}