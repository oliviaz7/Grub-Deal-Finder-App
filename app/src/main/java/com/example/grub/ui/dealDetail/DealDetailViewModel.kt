package com.example.grub.ui.dealDetail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.model.Deal
import com.example.grub.model.User
import com.example.grub.model.VoteType
import com.example.grub.model.mappers.RestaurantDealMapper.mapRawDealToDeal
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
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
    val showBottomSheet: Boolean,
    val currUser: User?,
    val deal: Deal?,
    val restaurantName: String?,
    val restaurantAddress: String?,
    val snackbarMessage: String?,
) {
    val isLoggedIn = currUser != null
}

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class DealDetailViewModelState(
    val showBottomSheet: Boolean,
    val currUser: User?,
    val deal: Deal?,
    val restaurantName: String?,
    val restaurantAddress: String?,
    val snackbarMessage: String?,
) {

    /**
     * Converts this [DealDetailViewModelState] into a more strongly typed [DealDetailUiState] for driving
     * the ui.
     */
    fun toUiState(): DealDetailUiState =
        DealDetailUiState(
            showBottomSheet,
            currUser,
            deal,
            restaurantName,
            restaurantAddress,
            snackbarMessage
        )
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
@RequiresApi(Build.VERSION_CODES.O)
class DealDetailViewModel(
    private val appViewModel: AppViewModel,
    private val restaurantDealRepo: RestaurantDealsRepository,
    private val deal: Deal?,
    private val restaurantName: String?,
    private val restaurantAddress: String?,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        DealDetailViewModelState(
            showBottomSheet = false,
            currUser = null,
            deal = null,
            restaurantName = "",
            restaurantAddress = "",
            snackbarMessage = null,
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

    private suspend fun observeChangesToDeal() {
        // subscribe to changes in the current deal's votes
        restaurantDealRepo.accumulatedDeals().collectLatest { allRestaurantDeals ->

            // for each restaurant, look through its deals until we find our deal
            allRestaurantDeals.map { restaurantDeal ->
                restaurantDeal.rawDeals.map { rawDeal ->
                    if (rawDeal.id == deal?.id) {

                        // update the view model state with any changes to the new deal
                        // if we add the ability to edit other fields, we need to include
                        viewModelState.update {
                            it.copy(
                                deal = mapRawDealToDeal(rawDeal)
                            )
                        }
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    deal = deal,
                    restaurantName = restaurantName,
                    restaurantAddress = restaurantAddress
                )
            }
            appViewModel.currentUser.collect { currentUser: User? ->
                viewModelState.update {
                    it.copy(
                        currUser = currentUser,
                    )
                }
            }
        }
        // subscribe to changes in the deal details that we're observing
        viewModelScope.launch {
            observeChangesToDeal()
        }
    }

    fun clearSnackBarMsg() {
        viewModelState.update {
            it.copy(snackbarMessage = null)
        }
    }

    fun setShowBottomSheet(show: Boolean) {
        viewModelState.update {
            it.copy(
                showBottomSheet = show,
            )
        }
    }

    fun onSaveClicked() {
        Log.d("deal Details", "save")
        if (viewModelState.value.currUser == null) {
            setShowBottomSheet(true)
        } else if (!viewModelState.value.deal!!.userSaved) {
            viewModelScope.launch {
                restaurantDealRepo.saveDeal(
                    dealId = viewModelState.value.deal!!.id,
                    userId = viewModelState.value.currUser!!.id,
                )
                viewModelState.update {
                    it.copy(
                        snackbarMessage = "Saved successfully!",
                    )
                }
            }
        } else if (viewModelState.value.deal!!.userSaved) {
            viewModelScope.launch {
                restaurantDealRepo.unsaveDeal(
                    dealId = viewModelState.value.deal!!.id,
                    userId = viewModelState.value.currUser!!.id,
                )
                viewModelState.update {
                    it.copy(
                        snackbarMessage = "Unsaved successfully!",
                    )
                }
            }
        }

    }

    fun onUpVoteClicked() {
        Log.d("deal Details", "upvote")
        if (viewModelState.value.currUser == null) {
            setShowBottomSheet(true)
        } else {
            val alreadyUpvoted = viewModelState.value.deal?.userVote == VoteType.UPVOTE
            viewModelScope.launch {
                restaurantDealRepo.updateVote(
                    dealId = viewModelState.value.deal!!.id,
                    userId = viewModelState.value.currUser!!.id,
                    userVote = if (alreadyUpvoted) VoteType.NEUTRAL else VoteType.UPVOTE,
                )
                viewModelState.update {
                    if (alreadyUpvoted) {
                        it.copy(
                            snackbarMessage = "Removed up vote successfully!",
                        )
                    } else {
                        it.copy(
                            snackbarMessage = "Up voted successfully!",
                        )
                    }
                }
            }
        }
    }

    fun onDownVoteClicked() {
        Log.d("deal Details", "downvote")
        if (viewModelState.value.currUser == null) {
            setShowBottomSheet(true)
        } else {
            val alreadyDownvoted = viewModelState.value.deal?.userVote == VoteType.DOWNVOTE
            viewModelScope.launch {
                restaurantDealRepo.updateVote(
                    dealId = viewModelState.value.deal!!.id,
                    userId = viewModelState.value.currUser!!.id,
                    userVote = if (alreadyDownvoted) VoteType.NEUTRAL else VoteType.DOWNVOTE,
                )
                viewModelState.update {
                    if (!alreadyDownvoted) {
                        it.copy(
                            snackbarMessage = "Down voted successfully!",
                        )
                    } else {
                        it.copy(
                            snackbarMessage = "Removed down vote successfully!",
                        )
                    }

                }
            }
        }
    }


    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            appViewModel: AppViewModel,
            restaurantDealRepo: RestaurantDealsRepository,
            deal: Deal?,
            restaurantName: String?,
            restaurantAddress: String?,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DealDetailViewModel(
                    appViewModel,
                    restaurantDealRepo,
                    deal,
                    restaurantName,
                    restaurantAddress,
                ) as T
            }
        }
    }


}