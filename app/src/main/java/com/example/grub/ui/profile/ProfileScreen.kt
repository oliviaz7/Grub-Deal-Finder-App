package com.example.grub.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

//import android.content.Intent
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.grub.data.auth.UserProfile
//import com.google.android.gms.common.api.ApiException

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