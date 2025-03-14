package com.example.grub.ui.profile


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign


@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

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
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {


                Image(
                    painter = rememberAsyncImagePainter(R.drawable.grub),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .shadow(8.dp, CircleShape, clip = false)
                        .clip(CircleShape)

                )


                Spacer(modifier = Modifier.height(24.dp))

                // Username
                Text(
                    text = uiState.currentUser.username,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Centered Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ProfileOption(Icons.Default.Favorite, "Favorite Deals")
                    ProfileOption(Icons.Default.Person, "Account Details")
                    ProfileOption(Icons.Default.Info, "About Grub")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "version 1.0.0.0", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { profileViewModel.onSignOut() }
                ) {
                    Text("Sign Out")
                }
            }

        } else {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.weight(6f)
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.weight(2f))
                        Text(
                            text = "Welcome to Grub!",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.weight(0.4f))
                        Text(
                            text = "Sign in to save deals, manage your profile, and more.",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.weight(4f))
                    }
                }
                GoogleSignInButton()
                Spacer(modifier = Modifier.weight(1f))
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
        Text("Sign in with Google", style = MaterialTheme.typography.titleMedium, color = Color.Black)
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

