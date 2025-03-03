package com.example.grub.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoggedIn && uiState.userProfile != null) {
        Column(modifier = modifier) {
            Text(uiState.userProfile.username)
            Text(uiState.userProfile.email)
        }
    } else {
        // TODO: show the login flow?
        // whatever we want to show when the user is not logged in and they click profile
    }
}