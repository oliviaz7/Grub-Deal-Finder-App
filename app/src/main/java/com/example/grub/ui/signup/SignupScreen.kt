package com.example.grub.ui.signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
        }
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
            // TODO: current UI is ass
            Text(
                text = "Welcome to Grub! Sign up to get started.",
                style = MaterialTheme.typography.labelLarge,
            )
            // TODO: validation that this is a single word and there's no whitespace
            TitledOutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChanged,
                label = "Username",
                text = null,
                placeholder = "eg. meow_deals_123",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
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
            // TODO: check if it's a valid email address
            TitledOutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                label = "Email",
                text = null,
                placeholder = "eg. example@gmail.com",
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
            )
            // TODO: come up with a placeholder for the password field
            TitledOutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = "Password",
                text = null,
                placeholder = "",
                modifier = Modifier.fillMaxWidth(),
                titleStyle = MaterialTheme.typography.labelLarge,
                optional = false,
            )

            // TODO: make the button disabled if any of the fields are empty
            // also all the fields must be valid
            Button(
                onClick = onSignupClicked,
                enabled = !uiState.isLoading,
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