package com.example.grub.ui.profile.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AboutPageUiState(
    val showBottomSheet: Boolean = true,
)

class AboutPageViewModel (
    private val appViewModel: AppViewModel,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AboutPageUiState())
    val uiState: StateFlow<AboutPageUiState> = _uiState.asStateFlow()

    companion object {
        fun provideFactory(
            appViewModel: AppViewModel,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AboutPageViewModel(
                    appViewModel,
                ) as T
            }
        }
    }
}