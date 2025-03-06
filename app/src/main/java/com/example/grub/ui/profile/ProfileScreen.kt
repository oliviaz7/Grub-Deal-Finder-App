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
    if (uiState.isLoggedIn && uiState.currentUser != null) {
        Column(modifier = modifier) {
            Text(uiState.currentUser.username)
            Text(uiState.currentUser.email)
        }
    } else {
        // TODO: show the login flow?
        // whatever we want to show when the user is not logged in and they click profile
    }
}