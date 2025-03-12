package com.example.grub.ui.profile


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.graphics.graphicsLayer
import com.example.grub.R
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape


@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = viewModel(),
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background
        Image(
            painter = rememberAsyncImagePainter(R.drawable.grub),
            contentDescription = "Logo Background",
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 100.dp)
                .graphicsLayer(alpha = 0.2f)
        )


        if (uiState.isLoggedIn && uiState.currentUser != null) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = rememberAsyncImagePainter(R.drawable.grub),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
               Image(
                    painter = rememberAsyncImagePainter(R.drawable.grub),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .shadow(8.dp, CircleShape, clip = false)
                        .clip(CircleShape)

              )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Username
                Text(
                    text = uiState.currentUser.username ?: "User",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(100.dp))

                // Centered Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProfileOption(Icons.Default.Favorite, "Favorite Deals")
                    ProfileOption(Icons.Default.Person, "Account Details")
                    ProfileOption(Icons.Default.Info, "About Grub")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "version 1.0.0.0", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(40.dp))

                GoogleSignInButton()
            }

        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GoogleSignInButton()
            }
        }
    }
}

@Composable
fun GoogleSignInButton(profileViewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current

    val onClick: () -> Unit = {
        profileViewModel.googleSignIn(context)
    }

    Button(onClick = onClick) {
        Text("Sign in with Google")
    }
}

@Composable
fun ProfileOption(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B00).copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = Color.Black)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}

