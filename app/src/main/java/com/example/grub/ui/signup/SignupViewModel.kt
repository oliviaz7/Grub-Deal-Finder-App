package com.example.grub.ui.signup

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

data class SignupUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val isLoading: Boolean = false, // Optional: track signup request state
    val error: String? = null // Optional: handle errors
)

// Define navigation events
sealed class SignupNavigationEvent {
    object NavigateBack : SignupNavigationEvent()
    // Add more events if needed, e.g., NavigateToProfile
}

class SignupViewModel(
    private val appViewModel: AppViewModel,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    // Channel for navigation events
    private val _navigationEvents = Channel<SignupNavigationEvent>(Channel.BUFFERED)
    val navigationEvents: Flow<SignupNavigationEvent> = _navigationEvents.receiveAsFlow()

    // Methods to update UI state from outlined text fields
    fun setUsername(username: String) {
        _uiState.update { currentState ->
            currentState.copy(username = username)
        }
    }

    fun setEmail(email: String) {
        _uiState.update { currentState ->
            currentState.copy(email = email)
        }
    }

    fun setPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(password = password)
        }
    }

    fun setFirstName(firstName: String) {
        _uiState.update { currentState ->
            currentState.copy(firstName = firstName)
        }
    }

    fun setLastName(lastName: String) {
        _uiState.update { currentState ->
            currentState.copy(lastName = lastName)
        }
    }

    // Method to trigger signup request
    fun signup() {
        viewModelScope.launch {
            val currentState = uiState.value
            // Check if all fields are filled (since they’re required)
            if (currentState.username.isBlank() ||
                currentState.email.isBlank() ||
                currentState.password.isBlank() ||
                currentState.firstName.isBlank() ||
                currentState.lastName.isBlank()
            ) {
                _uiState.update { it.copy(error = "All fields are required") }
                return@launch
            }

            // Set loading state
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Call the repository signup method
            val result = authRepository.createUserAccount(
                username = currentState.username,
                password = currentState.password,
                firstName = currentState.firstName,
                lastName = currentState.lastName,
                email = currentState.email
            )
            if (result is Result.Success) {
                _uiState.update { it.copy(isLoading = false) }
                // Emit navigation event to pop back
                _navigationEvents.send(SignupNavigationEvent.NavigateBack)

                // pop and go to the profile screen
            }
            // TODO: Handle error states
            if (result is Result.Error) {
                _uiState.update {
                    it.copy(
                        error = result.exception.message,
                        isLoading = false,
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
                return SignupViewModel(
                    appViewModel,
                    authRepository,
                ) as T
            }
        }
    }
}