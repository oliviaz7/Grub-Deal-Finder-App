package com.example.grub.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.auth.AuthRepository
import com.example.grub.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Serves as a centralized UI state holder for app-wide data (eg. isLoggedIn)
 * Provides state flows for other screens to observe
 *
 * Is the single source of truth for authentication data, functions, and user profile details
 */
class AppViewModel(
    private val authRepository: AuthRepository // Placeholder for future auth
) : ViewModel() {

    // Current logged-in user (null if not logged in)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.loggedInUser.collect { user ->
                _currentUser.value = user
            }
        }

        // TODO: OLIVIA ADD GET USER LOCATION HERE AND EXPOSE COORDINATE FLOWS
    }

    /**
     * Factory for AppViewModel that takes AuthRepository as a dependency
     */
    companion object {
        fun provideFactory(
            authRepository: AuthRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AppViewModel(authRepository) as T
            }
        }
    }
}