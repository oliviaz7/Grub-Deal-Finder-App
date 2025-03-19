package com.example.grub.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grub.data.auth.AuthRepository
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.grub.data.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

// Define navigation events
sealed class LoginNavigationEvent {
    object NavigateBack : LoginNavigationEvent()
    // Add more events if needed, e.g., NavigateToProfile
}

class LoginViewModel(
    private val appViewModel: AppViewModel,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Channel for navigation events
    private val _navigationEvents = Channel<LoginNavigationEvent>(Channel.BUFFERED)
    val navigationEvents: Flow<LoginNavigationEvent> = _navigationEvents.receiveAsFlow()

    // Methods to update UI state from outlined text fields
    fun setUsername(username: String) {
        _uiState.update { currentState ->
            currentState.copy(username = username)
        }
    }

    fun setPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(password = password)
        }
    }

    // Method to trigger login request
    fun login() {
        viewModelScope.launch {
            val currentState = uiState.value
            // Check if required fields are filled
            if (currentState.username.isBlank() || currentState.password.isBlank()) {
                _uiState.update { it.copy(error = "Username and password are required") }
                return@launch
            }

            // Set loading state
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Call the repository login method
            val result = authRepository.login(
                username = currentState.username,
                password = currentState.password
            )

            if (result is Result.Success) {
                _uiState.update { it.copy(isLoading = false) }
                // Emit navigation event to pop back
                _navigationEvents.send(LoginNavigationEvent.NavigateBack)
            }
            if (result is Result.Error) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Login failed"
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            appViewModel: AppViewModel,
            authRepository: AuthRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(
                    appViewModel,
                    authRepository,
                ) as T
            }
        }
    }
}