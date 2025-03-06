package com.example.grub.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.auth.AuthRepository
import com.example.grub.model.User
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.credentials.Credential

/**
 * UI state for the Map route.
 */
data class ProfileUiState(
    val currentUser: User? = null,
    val errorMessage: String? = null
) {
    val isLoggedIn = currentUser != null
}

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
            appViewModel.currentUser.collect { currentUser: User? ->
                _uiState.update {
                    // if currentUser is null, means no one is logged in
                    it.copy(
                        currentUser = currentUser,
                    )
                }
            }
        }
    }

    fun handleSignIn(credential: Credential) {
        viewModelScope.launch {
            // Handle Google sign-in with the provided credential
            authRepository.handleSignIn(credential)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
//            _uiState.update { it.copy(isLoggedIn = false, userProfile = null) }
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
