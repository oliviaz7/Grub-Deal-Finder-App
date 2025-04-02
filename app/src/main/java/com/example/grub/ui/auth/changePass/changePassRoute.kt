package com.example.grub.ui.auth.changePass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun ChangePassRoute(
    changePassViewModel: ChangePassViewModel,
    navController: NavController,
) {
    val uiState by changePassViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(changePassViewModel) {
        changePassViewModel.navigationEvents.collect { event ->
            when (event) {
                is ChangePassNavigationEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    ChangePassRoute(
        uiState = uiState,
        navController = navController,
        onOldPasswordChanged = { changePassViewModel.setOldPassword(it) },
        onNewPasswordChanged = { changePassViewModel.setNewPassword(it) },
        onConfirmPasswordChanged = { changePassViewModel.setConfirmPassword(it) },
        onUsernameChanged = { changePassViewModel.setUsername(it) },
        onChangePasswordClicked = { changePassViewModel.changePass() }
    )
}

@Composable
fun ChangePassRoute(
    uiState: ChangePassUiState,
    navController: NavController,
    onOldPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onChangePasswordClicked: () -> Unit,
) {
    ChangePassScreen(
        uiState = uiState,
        navController = navController,
        onNewPasswordChanged = onNewPasswordChanged,
        onOldPasswordChanged = onOldPasswordChanged,
        onConfirmPasswordChanged = onConfirmPasswordChanged,
        onUsernameChanged = onUsernameChanged,
        onChangePasswordClicked =  onChangePasswordClicked
    )
}