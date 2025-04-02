package com.example.grub.ui.profile.about

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.grub.R

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AboutPageScreen(
    uiState: AboutPageUiState,
    modifier: Modifier = Modifier,
    AboutPageViewModel: AboutPageViewModel = viewModel(),
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier.fillMaxSize()
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
                }
            },
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        ) { innerPadding ->
            // Background
            Image(
                painter = rememberAsyncImagePainter(R.drawable.grub),
                contentDescription = "Logo Background",
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 100.dp)
                    .graphicsLayer(alpha = 0.05f)
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Grub's Mission and Values",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)

                )

                Text(
                    text = "Grub helps you find restaurant deals and discounts in your area! " +
                            "We believe that everyone should be well informed of deals when you choose to eat out " +
                            "and we're here to help you find it :D ",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )

                Text(
                    text =  "We're committed to providing you with the best deals and discounts " +
                            "so you can enjoy your favorite foods without breaking the bank.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )


                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "How does Grub Work?",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                Text(
                    text = "When you spot a deal or discount, you can share it by posting it on Grub! " +
                            "Simply click on the orange plus button on the map or list view and select the restaurant you wish to add. " +
                            "Once selected, take a photo of the deal and Grub AI will fill in the deal details for you!! " +
                            "Fill in any missing information and now you can see your deal among thousands of others in your area :D"
                             ,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )

//                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Version 1.0.0.0",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}