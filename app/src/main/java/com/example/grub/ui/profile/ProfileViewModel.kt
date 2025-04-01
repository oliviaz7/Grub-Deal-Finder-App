package com.example.grub.ui.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.Result
import com.example.grub.data.auth.AuthRepository
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.successOr
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.User
import com.example.grub.model.mappers.RestaurantDealMapper
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * UI state for the Map route.
 */
data class ProfileUiState(
    val profileUser: User? = null, // the user whose profile is being viewed
    val errorMessage: String? = null,
    val favouriteDeals: List<RestaurantDeal> = emptyList(),
    val showBottomSheet: Boolean = false,
    val userId: String? = null, // the user id whose profile is being viewed
    val currUserId: String? = null // the logged in user
) {
    val isLoggedIn = currUserId != null
    val isCurrentUserProfile = currUserId == userId || (userId == null && currUserId != null)
    val isGuestUser = userId != null
}

data class ProfileViewModelState(
    val profileUser: User? = null,
    val errorMessage: String? = null,
    val favouriteDeals: List<RestaurantDeal>,
    val showBottomSheet: Boolean = false,
    val userId: String? = null,
    val currUserId: String? = null, // the logged in user
) {
    /**
     * Converts this [ProfileViewModelState] into a more strongly typed [ProfileUiState] for driving
     * the ui.
     */

    fun toUiState(): ProfileUiState =
        ProfileUiState(
            profileUser = profileUser,
            errorMessage = errorMessage,
            favouriteDeals = favouriteDeals,
            showBottomSheet = showBottomSheet,
            userId = userId,
            currUserId = currUserId,
        )
}

class ProfileViewModel(
    private val appViewModel: AppViewModel,
    private val authRepository: AuthRepository,
    private val restaurantRepo: RestaurantDealsRepository,
    private val dealMapper: RestaurantDealMapper,
    private val userId: String?,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState(
        favouriteDeals = emptyList(),
    ))

    // UI state exposed to the UI
    val uiState = viewModelState
        .map(ProfileViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            appViewModel.currentUser.collect { currentUser: User? ->
                viewModelState.update {
                    it.copy(
                        userId = userId,
                        currUserId = currentUser?.id
                    )
                }
                // If userId is null, we are viewing the current user's profile
                // otherwise, we are viewing another user's profile
                if (userId == null || userId == currentUser?.id) {
                    viewModelState.update {
                        it.copy(
                            profileUser = currentUser, // set the profile to the current user info
                        )
                    }
                } else {
                    // get the user info from the userId
                    getUserById(userId)
                    // TODO: handle the case where userId is not found
                }
            }
        }
    }

    private fun getUserById(userId: String) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Getting user: $userId")
            when (val result = authRepository.getUserById(userId)) {
                is Result.Success-> {
                    // Handle success, e.g., update UI state or notify user
                    val user = result.data
                    viewModelState.update { it.copy(profileUser = user) }
                }

                is Result.Error -> {
                    // Handle error, e.g., show error message
                    val error = "Error fetching profile: ${result.exception}"
                    Log.e("ProfileViewModel", error)
                    viewModelState.update { it.copy(errorMessage = error) }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun onClickFavDeals() {
        viewModelScope.launch {

            val userId = viewModelState.value.profileUser?.id // either the current user or the userId passed in
            if (userId == null) {
                Log.d("ProfileViewModel", "User ID is null")
                return@launch
            }
            try {
                val mappedDeals = restaurantRepo
                    .getSavedDeals(userId)
                    .successOr(emptyList()).map { dealMapper.mapResponseToRestaurantDeals(it) }

                viewModelState.update { it.copy(favouriteDeals = mappedDeals) }
            } catch (e: Exception) {
                Log.d("ProfileViewModel", "Error fetching favourite deals: ${e.message}")
            }
        }
        setShowBottomSheet(true)
    }

    fun onSignOut() {
        viewModelScope.launch {
            if (uiState.value.isCurrentUserProfile) {
                try {
                    authRepository.logout()
                } catch (e: Exception) {
                    Log.d("ProfileViewModel", "signout error ${e.message}")
                }
            }
        }
    }

    /**
     * Factory for ProfileViewModel that takes AppViewModel as a dependency
     */
    companion object {
        fun provideFactory(
            userId: String?,
            appViewModel: AppViewModel,
            authRepository: AuthRepository,
            restaurantRepo: RestaurantDealsRepository,
            dealMapper: RestaurantDealMapper,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(
                    appViewModel,
                    authRepository,
                    restaurantRepo,
                    dealMapper,
                    userId
                ) as T
            }
        }
    }
}
