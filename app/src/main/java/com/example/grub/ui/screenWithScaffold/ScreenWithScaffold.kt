package com.example.grub.ui.screenWithScaffold

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.grub.data.auth.AuthRepository
import com.example.grub.ui.AppViewModel
import com.example.grub.ui.fab.Fab
import com.example.grub.ui.navigation.BottomNavigation
import com.example.grub.ui.navigation.Destinations
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWithScaffold(
    navController: NavHostController,
    authRepository: AuthRepository? = null,
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
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sign in to post a deal", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    val context = LocalContext.current

                    Button(
                        onClick = {
                            val rawNonce = UUID.randomUUID().toString()
                            scope.launch {
                                try {
                                    authRepository?.googleSignInButton(context, rawNonce)
                                } catch (e: Exception) {
                                    Log.d("sign in bottom sheet", "failed lol: ${e.message}")
                                }
                            }
                            showBottomSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sign in with Google")
                    }
                }
            }
        }
    }
}
