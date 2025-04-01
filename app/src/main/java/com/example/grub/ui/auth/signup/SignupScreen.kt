package com.example.grub.ui.auth.signup

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.ui.addDealFlow.components.TitledOutlinedTextField

@Composable
fun SignupScreen(
    uiState: SignupUiState,
    navController: NavController,
    onUsernameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onSignupClicked: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { navController.popBackStack() },
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Create an Account",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Grub! Sign up to get started.",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TitledOutlinedTextField(
                value = uiState.username,
                onValueChange = { newValue ->
                    if (!newValue.contains(" ") && newValue.all { it.isLetterOrDigit() || it == '_' }) {
                        onUsernameChanged(newValue)
                    }
                },
                label = "Username",
                text = null,
                placeholder = "eg. meow_deals_123",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
                isError = uiState.username.isNotEmpty() &&
                        (uiState.username.contains(" ") || !uiState.username.all { it.isLetterOrDigit() || it == '_' })
            )

            TitledOutlinedTextField(
                value = uiState.firstName,
                onValueChange = onFirstNameChanged,
                label = "First Name",
                text = null,
                placeholder = "eg. John",
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
            )

            TitledOutlinedTextField(
                value = uiState.lastName,
                onValueChange = onLastNameChanged,
                label = "Last Name",
                text = null,
                placeholder = "eg. Doe",
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
            )

            TitledOutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                label = "Email",
                text = null,
                placeholder = "eg. example@gmail.com",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.email.isNotEmpty() && !isValidEmail(uiState.email)
            )

            TitledOutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = "Password",
                text = null,
                placeholder = "Enter at least 8 characters",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // also all the fields must be valid
            val isFormValid = uiState.username.isNotEmpty() &&
                    !uiState.username.contains(" ") &&
                    uiState.username.all { it.isLetterOrDigit() || it == '_' } &&
                    uiState.firstName.isNotEmpty() &&
                    uiState.lastName.isNotEmpty() &&
                    uiState.email.isNotEmpty() &&
                    isValidEmail(uiState.email) &&
                    uiState.password.isNotEmpty()

            Button(
                onClick = onSignupClicked,
                enabled = !uiState.isLoading && isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Sign Up")
                }
            }

            uiState.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// Helper function for email validation
private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}