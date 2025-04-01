package com.example.grub.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.grub.GrubApplication
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        // Install the splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)

        val appContainer = (application as GrubApplication).container
        setContent {
            GrubApp(appContainer)
        }
    }
}
