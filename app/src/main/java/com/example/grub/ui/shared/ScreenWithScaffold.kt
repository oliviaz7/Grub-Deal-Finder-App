package com.example.grub.ui.shared

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.grub.ui.AppViewModel
import com.example.grub.ui.shared.navigation.BottomNavigation
import com.example.grub.ui.shared.navigation.Destinations
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWithScaffold(
    navController: NavHostController,
    appViewModel: AppViewModel? = null,
    showBottomNavItem: Boolean = true,
    showFloatingActionButton: Boolean = true,
    content: @Composable () -> Unit,
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val currentUser by appViewModel?.currentUser?.collectAsState(initial = null)
        ?: remember { mutableStateOf(null) }

    val onFabClick: () -> Unit = {
        if (currentUser != null) {
            Log.d("screen w scaf", currentUser.toString())
            navController.navigate(Destinations.ADD_DEAL_ROUTE)
        } else {
            showBottomSheet = true
        }
    }
    Scaffold(
        floatingActionButton = {
            if (showFloatingActionButton) {
                Fab(onclick = onFabClick)
            }
        },
        floatingActionButtonPosition = FabPosition.Start,
        bottomBar = {
            if (showBottomNavItem) {
                BottomNavigation(navController)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
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
                    Text("Sign in to post a deal", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { navController.navigate(Destinations.LOGIN_ROUTE) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = { navController.navigate(Destinations.SIGNUP_ROUTE) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Sign up",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
