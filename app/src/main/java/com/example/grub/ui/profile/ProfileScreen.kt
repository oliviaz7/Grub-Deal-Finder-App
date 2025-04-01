package com.example.grub.ui.profile


import RestaurantItem
import android.os.Build
import android.util.Log
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
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.grub.R
import com.example.grub.model.User
import com.example.grub.model.VoteType
import com.example.grub.ui.shared.navigation.Destinations
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onClickFavDeals: () -> Unit,
    setShowBottomSheet: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    onSignOut: () -> Unit,
) {

    Log.d("ProfileScreen", "ProfileScreen: ${uiState.profileUser}")

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


        if (uiState.profileUser != null) {
            UserProfile(
                uiState = uiState,
                navController = navController,
                onClickFavDeals = onClickFavDeals,
                onSignOut = onSignOut,
                profileUser = uiState.profileUser,
            )
            if (uiState.showBottomSheet) {
                FavouriteDeals(
                    setShowBottomSheet,
                    uiState,
                    navController,
                )
            }
        } else {
            WelcomeScreen(navController)
        }
    }
}

@Composable
fun UserProfile(
    uiState: ProfileUiState,
    navController: NavController,
    onClickFavDeals: () -> Unit,
    onSignOut: () -> Unit,
    profileUser: User,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.mipmap.ic_grub_big_round),
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
            text = profileUser.username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // karma
        Row {
            Icon(
                Icons.Filled.ThumbUpOffAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(26.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${profileUser.upvote - profileUser.downvote}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                Icons.Filled.ThumbDownOffAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(26.dp),
            )

        }

        Spacer(modifier = Modifier.height(32.dp))

        // Centered Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ProfileOption(Icons.Default.Favorite, "Saved Deals", onClickFavDeals)
            ProfileOption(Icons.Default.Person, "Account Details")
            ProfileOption(Icons.Default.Info, "About Grub") {
                navController.navigate(Destinations.ABOUT_ROUTE)
            }

        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "version 1.0.0.0", fontSize = 12.sp, color = Color.Gray)
        if (uiState.isCurrentUserProfile) {
            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onSignOut
            ) {
                Text("Sign Out")
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.weight(6f)
        ) {
            Column(
                modifier = Modifier
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
                text = "${uiState.profileUser!!.username}'s Favourite Deals",
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

@Composable
fun AboutPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "About Grub",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Grub is an app that helps you find the best restaurant deals in your area. Save your favorite deals, manage your profile, and more.",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    text: String,
    onClickFavDeals: () -> Unit = {},
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
                .clickable {
                    onClickFavDeals()
                }
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = Color.Black)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}

