package com.example.grub.ui.profile.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grub.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

data class AboutPageUiState(
    val showBottomSheet: Boolean = true,
)

sealed class AboutPageNavigationEvent {
    object NavigateBack : AboutPageNavigationEvent()
}

class AboutPageViewModel (
    private val appViewModel: AppViewModel,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AboutPageUiState())
    val uiState: StateFlow<AboutPageUiState> = _uiState.asStateFlow()

    // Channel for navigation events
    private val _navigationEvents = Channel<AboutPageNavigationEvent>(Channel.BUFFERED)
    val navigationEvents: Flow<AboutPageNavigationEvent> = _navigationEvents.receiveAsFlow()

    fun setShowBottomSheet(show: Boolean) {
        _uiState.update {
            it.copy(showBottomSheet = show)
        }
    }


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