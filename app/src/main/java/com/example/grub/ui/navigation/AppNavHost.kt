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

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grub.data.AppContainer
import com.example.grub.model.Deal
import com.example.grub.model.mappers.RestaurantDealMapper
import com.example.grub.ui.AppViewModel
import com.example.grub.ui.addDealFlow.AddDealRoute
import com.example.grub.ui.addDealFlow.AddDealViewModel
import com.example.grub.ui.dealDetail.DealDetailRoute
import com.example.grub.ui.dealDetail.DealDetailViewModel
import com.example.grub.ui.fab.Fab
import com.example.grub.ui.list.ListRoute
import com.example.grub.ui.list.ListViewModel
import com.example.grub.ui.map.MapRoute
import com.example.grub.ui.map.MapViewModel
import com.example.grub.ui.profile.ProfileRoute
import com.example.grub.ui.profile.ProfileViewModel

object Destinations {
    const val HOME_ROUTE = "home"
    const val LIST_ROUTE = "list"
    const val ADD_DEAL_ROUTE = "selectRestaurant"
    const val DEAL_DETAIL_ROUTE = "deal"
    const val PROFILE_ROUTE = "profile"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.HOME_ROUTE,
) {
    val appViewModel: AppViewModel = viewModel(
        factory = AppViewModel.provideFactory(
            authRepository = appContainer.authRepository
        )
    )
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
                    restaurantDealsRepository = appContainer.restaurantDealsRepository,
                    dealMapper = RestaurantDealMapper,
                    fusedLocationProviderClient = appContainer.fusedLocationProviderClient
                )
            )
            ScreenWithScaffold(navController) {
                MapRoute(mapViewModel = mapViewModel)
            }
        }
        composable(Destinations.LIST_ROUTE) {
            val listViewModel: ListViewModel = viewModel(
                factory = ListViewModel.provideFactory(
                    restaurantDealsRepository = appContainer.restaurantDealsRepository,
                    dealMapper = RestaurantDealMapper,
                )
            )
            ScreenWithScaffold(navController) {
                ListRoute(listViewModel = listViewModel, navController)
            }
        }
        composable(Destinations.PROFILE_ROUTE) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.provideFactory(
                    appViewModel = appViewModel,
                    authRepository = appContainer.authRepository,
                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = true,
                showFloatingActionButton = false
            ) {
                ProfileRoute(profileViewModel)
            }
        }
        composable(Destinations.ADD_DEAL_ROUTE) {
            val addDealViewModel: AddDealViewModel = viewModel(
                factory = AddDealViewModel.provideFactory(
                    dealsRepository = appContainer.restaurantDealsRepository,
                    dealMapper = RestaurantDealMapper,
                    storageService = appContainer.storageService,
                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = false,
                showFloatingActionButton = false
            ) {
                AddDealRoute(
                    addDealViewModel = addDealViewModel,
                    navController = navController,
                )
            }
        }

        composable(
            Destinations.DEAL_DETAIL_ROUTE
        ) { backStackEntry ->
            val deal = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Deal>("deal") ?:null

            val dealDetailViewModel: DealDetailViewModel = viewModel(
                factory = DealDetailViewModel.provideFactory(
                    deal = deal
                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = true,
                showFloatingActionButton = false
            ) {
                DealDetailRoute(
                    dealDetailViewModel = dealDetailViewModel,
                    navController
                )
            }
        }
    }
}

@Composable
fun ScreenWithScaffold(
    navController: NavHostController,
    showBottomNavItem: Boolean = true,
    showFloatingActionButton: Boolean = true,
    content: @Composable () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            if (showFloatingActionButton) {
                Fab(onclick = { navController.navigate(Destinations.ADD_DEAL_ROUTE) })
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
    }
}
