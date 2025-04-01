package com.example.grub.ui.auth.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/**
 * Displays the Signup route.
 *
 * @param signupViewModel ViewModel that handles the business logic of this screen
 * @param navController Navigation controller for handling navigation
 */
@Composable
fun SignupRoute(
    signupViewModel: SignupViewModel,
    navController: NavController,
) {
    val uiState by signupViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(signupViewModel) {
        signupViewModel.navigationEvents.collect { event ->
            when (event) {
                is SignupNavigationEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    SignupScreen(
        uiState = uiState,
        navController = navController,
        onUsernameChanged = signupViewModel::setUsername,
        onEmailChanged = signupViewModel::setEmail,
        onPasswordChanged = signupViewModel::setPassword,
        onFirstNameChanged = signupViewModel::setFirstName,
        onLastNameChanged = signupViewModel::setLastName,
        onSignupClicked = signupViewModel::signup
    )
}
