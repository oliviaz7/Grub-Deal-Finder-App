package com.example.grub.ui.about

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.grub.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AboutPageScreen(
    uiState: AboutPageUiState,
    modifier: Modifier = Modifier,
    AboutPageViewModel: AboutPageViewModel = viewModel(),
    navController: NavController
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
                .graphicsLayer(alpha = 0.1f)
        )

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
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Grub's Mission and Values",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Grub helps you find food deals and discounts in your area! " +
                        "We believe that everyone should be well informed of deals in their area " +
                        "and we're here to help you find it :D " +
                        "We're committed to providing you with the best deals and discounts " +
                        "so you can enjoy your favorite foods without breaking the bank.",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "How does Grub Work?",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "When you spot a deal or discount, you can share it by posting it on Grub." +
                        "IM TOO TIRED TO WRITE THIS LET ME CODE MONEY FCKKKK I DO LATER",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Version 1.0.0.0",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 12.sp
            )

        }
    }
}