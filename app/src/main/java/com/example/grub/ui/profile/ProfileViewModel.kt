package com.example.grub.ui.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.auth.AuthRepository
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.successOr
import com.example.grub.model.RestaurantDeal
import com.example.grub.model.User
import com.example.grub.model.mappers.RestaurantDealMapper
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * UI state for the Map route.
 */
data class ProfileUiState(
    val currentUser: User? = null,
    val errorMessage: String? = null,
    val favouriteDeals: List<RestaurantDeal> = emptyList(),
    val showBottomSheet: Boolean = false,
) {
    val isLoggedIn = currentUser != null
}

class ProfileViewModel(
    private val appViewModel: AppViewModel,
    private val authRepository: AuthRepository,
    private val restaurantRepo: RestaurantDealsRepository,
    private val dealMapper: RestaurantDealMapper,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // TODO: lol remove me once auth is implemented
        viewModelScope.launch {
            appViewModel.currentUser.collect { currentUser: User? ->
                _uiState.update {
                    it.copy(
                        currentUser = currentUser,
                    )
                }
            }
        }
    }

    fun setShowBottomSheet(show: Boolean) {
        _uiState.update {
            it.copy(
                showBottomSheet = show,
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onClickFavDeals() {
        viewModelScope.launch {
            try {
                val mappedDeals = restaurantRepo
                    .getSavedDeals(_uiState.value.currentUser!!.id)
                    .successOr(emptyList()).map { dealMapper.mapResponseToRestaurantDeals(it) }

                _uiState.update { it.copy(favouriteDeals = mappedDeals) }
            } catch (e: Exception) {
                Log.d("ProfileViewModel", "Error fetching favourite deals: ${e.message}")
            }
        }
        setShowBottomSheet(true)
    }

    fun onSignOut() {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (e: Exception) {
                Log.d("profile vm", "signout error ${e.message}")
            }
        }
    }

    /**
     * Factory for ProfileViewModel that takes AppViewModel as a dependency
     */
    companion object {
        fun provideFactory(
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
                    dealMapper
                ) as T
            }
        }
    }
}
