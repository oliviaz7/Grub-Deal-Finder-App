package com.example.grub.ui.shared.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.grub.ui.auth.login.LoginRoute
import com.example.grub.ui.auth.login.LoginViewModel
import com.example.grub.ui.auth.signup.SignupRoute
import com.example.grub.ui.auth.signup.SignupViewModel
import com.example.grub.ui.dealDetail.DealDetailRoute
import com.example.grub.ui.dealDetail.DealDetailViewModel
import com.example.grub.ui.dealDetail.dealImage.DealImageViewModel
import com.example.grub.ui.list.DealImageRoute
import com.example.grub.ui.list.ListRoute
import com.example.grub.ui.list.ListViewModel
import com.example.grub.ui.map.MapRoute
import com.example.grub.ui.map.MapViewModel
import com.example.grub.ui.profile.ProfileRoute
import com.example.grub.ui.profile.ProfileViewModel
import com.example.grub.ui.profile.about.AboutPageRoute
import com.example.grub.ui.profile.about.AboutPageViewModel
import com.example.grub.ui.profile.account.AccountDetailsViewModel
import com.example.grub.ui.profile.accountDetails.AccountDetailsRoute
import com.example.grub.ui.shared.ScreenWithScaffold

object Destinations {
    const val HOME_ROUTE = "home"
    const val LIST_ROUTE = "list"
    const val ADD_DEAL_ROUTE = "selectRestaurant"
    const val DEAL_DETAIL_ROUTE = "deal"
    const val PROFILE_ROUTE = "profile"
    const val SIGNUP_ROUTE = "signup"
    const val LOGIN_ROUTE = "login"
    const val IMAGE_ROUTE = "image"
    const val ABOUT_ROUTE = "about"
    const val ACCOUNT_DETAILS = "account"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.HOME_ROUTE,
) {
    LaunchedEffect (Unit) {
        appContainer.authRepository.checkSavedCredentials()
    }

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
                appViewModel = appViewModel
            ) {
                ListRoute(listViewModel = listViewModel, navController)
            }
        }
        composable("${Destinations.PROFILE_ROUTE}?userId={userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") // Might be null

            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.provideFactory(
                    userId = userId,
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
        ) { _ ->
            val deal = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Deal>("deal")
            val restaurantName = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("restaurantName")
            val restaurantAddress = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("restaurantAddress")

            val dealDetailViewModel: DealDetailViewModel = viewModel(
                factory = DealDetailViewModel.provideFactory(
                    deal = deal,
                    restaurantDealRepo = appContainer.restaurantDealsRepository,
                    restaurantName = restaurantName,
                    restaurantAddress = restaurantAddress,
                    appViewModel = appViewModel,
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
            val aboutPageViewModel: AboutPageViewModel = viewModel(
                factory = AboutPageViewModel.provideFactory(
                    appViewModel = appViewModel,

                    )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = true,
                showFloatingActionButton = false
            ) {
                AboutPageRoute(aboutPageViewModel, navController)
            }
        }

        composable(
            Destinations.IMAGE_ROUTE
        ) { _ ->
            val dealImageURL = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("dealImageURL")
            val dealImageViewModel: DealImageViewModel = viewModel(
                factory = DealImageViewModel.provideFactory(
                    dealURL = dealImageURL,
                )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = false,
                showFloatingActionButton = false
            ) {
                DealImageRoute(
                    dealImageViewModel = dealImageViewModel,
                    navController = navController,
                )
            }
        }

        composable(route = Destinations.ACCOUNT_DETAILS) {
            val accountDetailsViewModel: AccountDetailsViewModel = viewModel(
                factory = AccountDetailsViewModel.provideFactory(
                    appViewModel = appViewModel,

                    )
            )
            ScreenWithScaffold(
                navController,
                showBottomNavItem = true,
                showFloatingActionButton = false
            ) {
                AccountDetailsRoute(accountDetailsViewModel, navController)
            }
        }

    }
}
