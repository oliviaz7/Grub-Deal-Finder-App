package com.example.grub.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.grub.ui.profile.ProfileViewModel
import androidx.compose.material3.Button
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.credentials.Credential


@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    Column(
        modifier = modifier.padding(start = 12.dp)) {
        if (uiState.isLoggedIn && uiState.userProfile != null) {
            Text("Welcome, ${uiState.userProfile.username}")
            Text("Email: ${uiState.userProfile.email}")

            Button(onClick = { ProfileViewModel.logout() }) {
                Text("Logout")
            }
        } else {
            Text("Please sign in")

            Button(onClick = {
                // Dummy Credential (Replace with real auth flow)
                val dummyCredential = Credential("dummy_id")
                ProfileViewModel.handleSignIn(dummyCredential)
            }) {
                Text("Sign in with Google")
            }
        }
    }
}