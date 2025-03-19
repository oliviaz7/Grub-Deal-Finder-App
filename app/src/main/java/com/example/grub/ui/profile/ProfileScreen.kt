package com.example.grub.ui.profile


import RestaurantItem
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.grub.R
import com.example.grub.ui.navigation.Destinations
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onClickFavDeals: () -> Unit,
    setShowBottomSheet: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = viewModel(),
    navController: NavController,
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
                    ProfileOption(Icons.Default.Favorite, "Favourite Deals", onClickFavDeals)
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
            FavouriteDeals(
                setShowBottomSheet,
                uiState,
                navController,
            )
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
//                GoogleSignInButton()
                Button(onClick = { navController.navigate(Destinations.LOGIN_ROUTE) }) {
                    Text(
                        "Login",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
                Button(onClick = { navController.navigate(Destinations.SIGNUP_ROUTE) }) {
                    Text(
                        "Sign up",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteDeals(
    setShowBottomSheet: (Boolean) -> Unit,
    uiState: ProfileUiState,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    if (uiState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    setShowBottomSheet(false)
                }
            },
            sheetState = sheetState,
            dragHandle = null,
            containerColor = Color.White,

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "${uiState.currentUser!!.username}'s Favourite Deals",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )
                if (uiState.favouriteDeals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No deals found",
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    val scrollState = rememberScrollState()
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        // Display the list of filtered restaurant deals
                        uiState.favouriteDeals.forEach { restaurant ->
                            RestaurantItem(
                                restaurant = restaurant,
                                navController = navController,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
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
        Text(
            "Sign in with Google",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
    }
}

@Composable
fun ProfileOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClickFavDeals: () -> Unit = {}
) {
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
                .clickable { onClickFavDeals() }
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = Color.Black)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}

