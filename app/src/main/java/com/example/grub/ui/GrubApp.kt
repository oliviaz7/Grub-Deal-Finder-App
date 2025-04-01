package com.example.grub.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.grub.data.AppContainer
import com.example.grub.ui.navigation.AppNavHost
import com.example.grub.ui.theme.ThemeProvider

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GrubApp(
    appContainer: AppContainer,
) {
    ThemeProvider {
        val navController = rememberNavController()

        AppNavHost(
            appContainer = appContainer,
            navController = navController,
        )
    }
}