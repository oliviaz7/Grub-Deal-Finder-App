package com.example.grub.ui.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun LoginRoute(
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(loginViewModel) {
        loginViewModel.navigationEvents.collect { event ->
            when (event) {
                is LoginNavigationEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    LoginRoute(
        uiState = uiState,
        navController = navController,
        onUsernameChanged = { loginViewModel.setUsername(it) },
        onPasswordChanged = { loginViewModel.setPassword(it) },
        onLoginClicked = { loginViewModel.login() }
    )
}

@Composable
fun LoginRoute(
    uiState: LoginUiState,
    navController: NavController,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
) {
    LoginScreen(
        uiState = uiState,
        navController = navController,
        onUsernameChanged = onUsernameChanged,
        onPasswordChanged = onPasswordChanged,
        onLoginClicked = onLoginClicked
    )
}