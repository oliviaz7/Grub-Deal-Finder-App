package com.example.grub.ui.profile

import java.util.UUID

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
import android.content.Context



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
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // TODO: lol remove me once auth is implemented
        // this is just a stub to force login so we can create the profile UI
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

    // States for loading, success, and error
    private val _googleSignInState = MutableStateFlow<GoogleSignInState>(GoogleSignInState.Idle)
    val googleSignInState: StateFlow<GoogleSignInState> get() = _googleSignInState

    fun googleSignIn(context: Context) {
        _googleSignInState.value = GoogleSignInState.Loading

        val rawNonce = UUID.randomUUID().toString()

        viewModelScope.launch {
            try {
                authRepository.googleSignInButton(context, rawNonce)
                _googleSignInState.value = GoogleSignInState.Success("Sign-in successful!")
            } catch (e: Exception) {
                _googleSignInState.value = GoogleSignInState.Error("Sign-in failed: ${e.message}")
            }
        }
    }

    // States for Google Sign-In
    sealed class GoogleSignInState {
        object Idle : GoogleSignInState()
        object Loading : GoogleSignInState()
        data class Success(val message: String) : GoogleSignInState()
        data class Error(val message: String) : GoogleSignInState()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
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
