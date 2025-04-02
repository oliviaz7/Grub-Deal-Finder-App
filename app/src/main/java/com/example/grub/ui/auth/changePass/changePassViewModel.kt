package com.example.grub.ui.auth.changePass

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

data class ChangePassUiState(
    val username: String = "",
    val password: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ChangePassNavigationEvent {
    object NavigateBack : ChangePassNavigationEvent()
}

class ChangePassViewModel(
    private val appViewModel: AppViewModel,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePassUiState())
    val uiState: StateFlow<ChangePassUiState> = _uiState.asStateFlow()

    private val _navigationEvents = Channel<ChangePassNavigationEvent>(Channel.BUFFERED)
    val navigationEvents: Flow<ChangePassNavigationEvent> = _navigationEvents.receiveAsFlow()


    fun setOldPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(password = password)
        }
    }

    fun setNewPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(newPassword = password)
        }
    }

    fun setConfirmPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(confirmPassword = password)
        }
    }

    fun setUsername(username: String) {
        _uiState.update { currentState ->
            currentState.copy(username = username)
        }
    }

    fun changePass() {
        viewModelScope.launch {
            val currentState = uiState.value

            if (currentState.password.isBlank()) {
                _uiState.update { it.copy(error = "Password is required") }
                return@launch
            }

            // Set loading state
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Call the repository changePassword method
            val result = authRepository.changePassword(
                username = currentState.username,
                newPassword = currentState.newPassword,
                oldPassword = currentState.password,
                confirmPassword = currentState.confirmPassword
            )

            if (result is Result.Success) {
                _uiState.update { it.copy(isLoading = false) }
                _navigationEvents.send(ChangePassNavigationEvent.NavigateBack)
            }
            if (result is Result.Error) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Change Password Failed"
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
                return ChangePassViewModel(
                    appViewModel,
                    authRepository,
                ) as T
            }
        }
    }
}