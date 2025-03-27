package com.example.grub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.grub.ui.about.AboutPageRoute
import com.example.grub.ui.about.AboutPageViewModel
import com.example.grub.ui.addDealFlow.AddDealRoute
import com.example.grub.ui.addDealFlow.AddDealViewModel
import com.example.grub.ui.dealDetail.DealDetailRoute
import com.example.grub.ui.dealDetail.DealDetailViewModel
import com.example.grub.ui.list.ListRoute
import com.example.grub.ui.list.ListViewModel
import com.example.grub.ui.login.LoginRoute
import com.example.grub.ui.login.LoginViewModel
import com.example.grub.ui.map.MapRoute
import com.example.grub.ui.map.MapViewModel
import com.example.grub.ui.profile.ProfileRoute
import com.example.grub.ui.profile.ProfileViewModel
import com.example.grub.ui.screenWithScaffold.ScreenWithScaffold
import com.example.grub.ui.signup.SignupRoute
import com.example.grub.ui.signup.SignupViewModel

object Destinations {
    const val HOME_ROUTE = "home"
    const val LIST_ROUTE = "list"
    const val ADD_DEAL_ROUTE = "selectRestaurant"
    const val DEAL_DETAIL_ROUTE = "deal"
    const val PROFILE_ROUTE = "profile"
    const val SIGNUP_ROUTE = "signup"
    const val LOGIN_ROUTE = "login"
    const val ABOUT_ROUTE = "about"
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
            authRepository = appContainer.authRepository,
            fusedLocationProviderClient = appContainer.fusedLocationProviderClient,
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
                    appViewModel = appViewModel,
                )
            )
            ScreenWithScaffold(
                navController,
                authRepository = appContainer.authRepository,
                appViewModel = appViewModel
            ) {
                MapRoute(mapViewModel = mapViewModel, navController)
            }
        }
        composable(Destinations.LIST_ROUTE) {
            val listViewModel: ListViewModel = viewModel(
                factory = ListViewModel.provideFactory(
                    restaurantDealsRepository = appContainer.restaurantDealsRepository,
                    appViewModel = appViewModel,
                    dealMapper = RestaurantDealMapper,
                )
            )
            ScreenWithScaffold(
                navController,
                authRepository = appContainer.authRepository,
                appViewModel = appViewModel
            ) {
                ListRoute(listViewModel = listViewModel, navController)
            }
        }
        composable(Destinations.PROFILE_ROUTE) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.provideFactory(
                    appViewModel = appViewModel,
                    authRepository = appContainer.authRepository,
                    dealMapper = RestaurantDealMapper,
                    restaurantRepo = appContainer.restaurantDealsRepository,
                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = true,
                showFloatingActionButton = false
            ) {
                ProfileRoute(profileViewModel, navController)
            }
        }
        composable(Destinations.SIGNUP_ROUTE) {
            val signupViewModel: SignupViewModel = viewModel(
                factory = SignupViewModel.provideFactory(
                    appViewModel = appViewModel,
                    authRepository = appContainer.authRepository,
                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = true,
                showFloatingActionButton = false
            ) {
                SignupRoute(signupViewModel, navController)
            }
        }
        composable(Destinations.LOGIN_ROUTE) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.provideFactory(
                    appViewModel = appViewModel,
                    authRepository = appContainer.authRepository,
                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = true,
                showFloatingActionButton = false
            ) {
                LoginRoute(loginViewModel, navController)
            }
        }
        composable(Destinations.ADD_DEAL_ROUTE) {
            val addDealViewModel: AddDealViewModel = viewModel(
                factory = AddDealViewModel.provideFactory(
                    dealsRepository = appContainer.restaurantDealsRepository,
                    dealMapper = RestaurantDealMapper,
                    storageService = appContainer.storageService,
                    appViewModel = appViewModel,
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
                ?.get<Deal>("deal") ?: null
            val restaurantName = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("restaurantName") ?: null
            val restaurantAddress = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("restaurantAddress") ?: null

            val dealDetailViewModel: DealDetailViewModel = viewModel(
                factory = DealDetailViewModel.provideFactory(
                    deal = deal,
                    restaurantDealRepo = appContainer.restaurantDealsRepository,
                    restaurantName = restaurantName,
                    restaurantAddress = restaurantAddress,
                    appViewModel = appViewModel,
                    authRepository = appContainer.authRepository,
                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = false,
                showFloatingActionButton = false
            ) {
                DealDetailRoute(
                    dealDetailViewModel = dealDetailViewModel,
                    navController
                )
            }
        }

        composable(route = Destinations.ABOUT_ROUTE) {
            val AboutPageViewModel: AboutPageViewModel = viewModel(
                factory = AboutPageViewModel.provideFactory(
                    appViewModel = appViewModel,

                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = true,
                showFloatingActionButton = false
            ) {
                AboutPageRoute(AboutPageViewModel, navController)
            }
        }
    }
}
