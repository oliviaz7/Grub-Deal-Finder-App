package com.example.grub.ui.dealDetail

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.auth.AuthRepository
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.model.Deal
import com.example.grub.model.User
import com.example.grub.model.VoteType
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

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
)

/**
 * An internal representation of the map route state, in a raw form
 * THIS ONLY BECOMES RELEVANT WHEN OUR THING BECOMES MORE COMPLEX
 */
private data class DealDetailViewModelState(
    val showBottomSheet: Boolean,
    val currUser: User?,
    val deal: Deal?,
    val restaurantName: String?,
    val restaurantAddress: String?
) {

    /**
     * Converts this [DealDetailViewModelState] into a more strongly typed [DealDetailUiState] for driving
     * the ui.
     */
    fun toUiState(): DealDetailUiState =
        DealDetailUiState(showBottomSheet, currUser, deal, restaurantName, restaurantAddress)
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
    private val authRepository: AuthRepository?,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        DealDetailViewModelState(
            showBottomSheet = false,
            currUser = null,
            deal = null,
            restaurantName = "",
            restaurantAddress = ""
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
            }
        } else if (viewModelState.value.deal!!.userSaved) {
            viewModelScope.launch {
                restaurantDealRepo.unsaveDeal(
                    dealId = viewModelState.value.deal!!.id,
                    userId = viewModelState.value.currUser!!.id,
                )
            }
        }

    }

    fun onUpVoteClicked() {
        Log.d("deal Details", "upvote")
        if (viewModelState.value.currUser == null) {
            setShowBottomSheet(true)
        } else {
            viewModelScope.launch {
                restaurantDealRepo.updateVote(
                    dealId = viewModelState.value.deal!!.id,
                    userId = viewModelState.value.currUser!!.id,
                    userVote = VoteType.UPVOTE,
                )
            }
        }
    }

    fun onDownVoteClicked() {
        Log.d("deal Details", "downvote")
        if (viewModelState.value.currUser == null) {
            setShowBottomSheet(true)
        } else {
            viewModelScope.launch {
                restaurantDealRepo.updateVote(
                    dealId = viewModelState.value.deal!!.id,
                    userId = viewModelState.value.currUser!!.id,
                    userVote = VoteType.DOWNVOTE,
                )
            }
        }
    }

    fun onLogin(context: Context) {
        Log.d("deal Details", "login")
        viewModelScope.launch {
            try {
                val rawNonce = UUID.randomUUID().toString()
                authRepository?.googleSignInButton(context, rawNonce)
            } catch (e: Exception) {
                Log.d("sign in bottom sheet", "failed lol: ${e.message}")
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
            authRepository: AuthRepository?,
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
                    authRepository,
                ) as T
            }
        }
    }


}