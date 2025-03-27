package com.example.grub.ui.addDealFlow.screens

import RestaurantItem
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.grub.ui.addDealFlow.AddDealUiState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.grub.ui.addDealFlow.Step
import com.example.grub.ui.addDealFlow.components.Loading
import com.example.grub.data.Result
import com.example.grub.ui.addDealFlow.components.CameraCaptureScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowExistingDeals(
    uiState: AddDealUiState,
    navController: NavController,
    getRestaurantDeals: () -> Unit,
    nextStep : (Step?) -> Unit,
    prevStep: (Step?) -> Unit,
    modifier: Modifier = Modifier
) {
    // fetch the restaurant
    // if the restuarant does not exist in the DB, skip this step
    // else show the deals using restaurantItem (see map screen)

    LaunchedEffect (Unit) {
        getRestaurantDeals()
    }

    if (uiState.restaurantDealLoading) {
        Loading()
    } else {
        ShowRestaurantDeals(
            uiState = uiState,
            navController = navController,
            nextStep = nextStep,
            prevStep = prevStep,
            modifier = modifier,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowRestaurantDeals (
    uiState: AddDealUiState,
    navController: NavController,
    nextStep : (Step?) -> Unit,
    prevStep: (Step?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Current Deals")
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { prevStep(null) },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        nextStep(null)
                    },
                ) {
                    Text("Next")
                }
            }
        },
        containerColor = Color.White,
        modifier = modifier
            .imePadding()
            .pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RestaurantItem(
                restaurant = uiState.selectedRestaurant,
                navController = navController,
                showBoxShadow = false,
                modifier = Modifier.padding(top = 16.dp)
            )

        }
    }
}