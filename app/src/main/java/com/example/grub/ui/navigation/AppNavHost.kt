/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.grub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grub.data.AppContainer
import com.example.grub.ui.map.MapRoute
import com.example.grub.ui.map.MapViewModel
import com.example.grub.ui.interests.InterestsRoute
import com.example.grub.ui.interests.InterestsViewModel

object Destinations {
    const val HOME_ROUTE = "home"
    const val INTERESTS_ROUTE = "interests"
}

@Composable
fun AppNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.HOME_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = Destinations.HOME_ROUTE,
        ) { _ ->
            val mapViewModel: MapViewModel = viewModel(
                factory = MapViewModel.provideFactory(
                    dealsRepository = appContainer.dealsRepository,
                )
            )
            MapRoute(
                mapViewModel = mapViewModel,
            )
        }
        composable(Destinations.INTERESTS_ROUTE) {
            val interestsViewModel: InterestsViewModel = viewModel(
                factory = InterestsViewModel.provideFactory(appContainer.interestsRepository)
            )
            InterestsRoute(
                interestsViewModel = interestsViewModel,
            )
        }
    }
}
