/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grub.ui

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.grub.ui.navigation.BottomNavigation
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.grub.data.AppContainer
import com.example.grub.ui.fab.Fab
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

//        Scaffold(
//            floatingActionButton = { Fab(onclick = { println("CLICKED BUTTON") }) },
//            bottomBar = { BottomNavigation(navController) },
//
//
//        ) { padding ->
//            AppNavHost(
//                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
//                appContainer = appContainer,
//                navController = navController,
//            )
//        }
    }
}