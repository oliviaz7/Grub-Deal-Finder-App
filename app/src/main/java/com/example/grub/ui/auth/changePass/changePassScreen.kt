package com.example.grub.ui.auth.changePass

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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.ui.addDealFlow.components.TitledOutlinedTextField

@Composable
fun ChangePassScreen(
    uiState: ChangePassUiState,
    navController: NavController,
    onOldPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onChangePasswordClicked: () -> Unit,
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
                    text = "Change Password",
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
                text = "Change your account password below.",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Add Username Field
            TitledOutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChanged,
                label = "Username",
                text = null,
                placeholder = "Enter your username",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
                isError = uiState.username.isNotEmpty()
            )

            // Current Password
            TitledOutlinedTextField(
                value = uiState.password,
                onValueChange = onOldPasswordChanged,
                label = "Current Password",
                text = null,
                placeholder = "Enter your current password",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            // New Password
            TitledOutlinedTextField(
                value = uiState.newPassword,
                onValueChange = onNewPasswordChanged,
                label = "New Password",
                text = null,
                placeholder = "Enter a new password",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            // Confirm New Password
            TitledOutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                label = "Confirm New Password",
                text = null,
                placeholder = "Re-enter your new password",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = uiState.confirmPassword.isNotEmpty() && uiState.confirmPassword != uiState.newPassword
            )

            // Validate form
            val isFormValid = uiState.username.isNotEmpty() &&
                    uiState.password.isNotEmpty() &&
                    uiState.newPassword.isNotEmpty() &&
                    uiState.confirmPassword.isNotEmpty() &&
                    uiState.newPassword == uiState.confirmPassword

            Button(
                onClick = onChangePasswordClicked,
                enabled = !uiState.isLoading && isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Change Password")
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
