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

    LoginScreen(
        uiState = uiState,
        navController = navController,
        onUsernameChanged = loginViewModel::setUsername,
        onPasswordChanged = loginViewModel::setPassword,
        onLoginClicked = loginViewModel::login
    )
}