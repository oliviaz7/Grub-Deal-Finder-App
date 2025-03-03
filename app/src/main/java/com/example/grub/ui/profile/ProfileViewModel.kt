package com.example.grub.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.Result
import com.example.grub.data.auth.AuthRepository
import com.example.grub.data.auth.UserProfile
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
    val isLoggedIn: Boolean = false,
    val userProfile: UserProfile? = null,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val appViewModel: AppViewModel,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // TODO: lol remove me once auth is implemented
        // this is just a stub to force login so we can create the profile UI
        viewModelScope.launch {
            authRepository.login("any_username", "any_pwd")
        }

        viewModelScope.launch {
            appViewModel.isLoggedIn.collect { isLoggedIn: Boolean ->
                _uiState.update {
                    it.copy(
                        // use isLoggedIn state for conditional UI rendering
                        isLoggedIn = isLoggedIn,
                    )
                }
                if (isLoggedIn) {
                    authRepository.getUserProfile().let { result ->
                        when (result) {
                            is Result.Success -> {
                                _uiState.update {
                                    it.copy(
                                        userProfile = result.data,
                                    )
                                }
                            }
                            else -> Log.e("FetchingError", "ProfileVM, failed to get user profile")
                        }
                    }
                }
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
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(appViewModel, authRepository) as T
            }
        }
    }
}
