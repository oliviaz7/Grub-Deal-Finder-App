package com.example.grub.ui.signup

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

    SignupRoute(
        uiState = uiState,
        navController = navController,
        onUsernameChanged = { signupViewModel.setUsername(it) },
        onEmailChanged = { signupViewModel.setEmail(it) },
        onPasswordChanged = { signupViewModel.setPassword(it) },
        onFirstNameChanged = { signupViewModel.setFirstName(it) },
        onLastNameChanged = { signupViewModel.setLastName(it) },
        onSignupClicked = { signupViewModel.signup() }
    )
}

/**
 * Displays the Signup route.
 *
 * @param uiState (state) the data to show on the screen
 * @param navController Navigation controller for handling navigation
 * @param onUsernameChanged Callback to update username
 * @param onEmailChanged Callback to update email
 * @param onPasswordChanged Callback to update password
 * @param onFirstNameChanged Callback to update first name
 * @param onLastNameChanged Callback to update last name
 * @param onSignupClicked Callback to trigger signup
 */
@Composable
fun SignupRoute(
    uiState: SignupUiState,
    navController: NavController,
    onUsernameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onSignupClicked: () -> Unit,
) {
    SignupScreen(
        uiState = uiState,
        navController = navController,
        onUsernameChanged = onUsernameChanged,
        onEmailChanged = onEmailChanged,
        onPasswordChanged = onPasswordChanged,
        onFirstNameChanged = onFirstNameChanged,
        onLastNameChanged = onLastNameChanged,
        onSignupClicked = onSignupClicked
    )
}