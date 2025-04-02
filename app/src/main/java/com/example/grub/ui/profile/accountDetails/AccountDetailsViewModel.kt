package com.example.grub.ui.profile.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import com.example.grub.model.User
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AccountDetailsUiState(
    val userName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val showBottomSheet: Boolean = false
)

class AccountDetailsViewModel(
    private val appViewModel: AppViewModel,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountDetailsUiState())
    val uiState: StateFlow<AccountDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appViewModel.currentUser.collect { user: User? ->
                _uiState.update {
                    it.copy(
                        userName = user?.username ?: "Unknown",
                        email = user?.email ?: "No email available",
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            appViewModel: AppViewModel,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AccountDetailsViewModel(appViewModel) as T
            }
        }
    }
}
