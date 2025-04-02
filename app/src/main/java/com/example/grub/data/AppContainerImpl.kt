package com.example.grub.data

import android.content.Context
import com.example.grub.data.auth.AuthRepository
import com.example.grub.data.auth.impl.AuthRepositoryImpl
import com.example.grub.data.auth.impl.FakeAuthRepositoryImpl
import com.example.grub.data.deals.RestaurantDealsRepository
import com.example.grub.data.deals.impl.FakeRestaurantDealsRepository
import com.example.grub.data.deals.impl.RestaurantDealsRepositoryImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val restaurantDealsRepository: RestaurantDealsRepository
    val storageService: StorageService
    val fusedLocationProviderClient: FusedLocationProviderClient
    val authRepository: AuthRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val restaurantDealsRepository: RestaurantDealsRepository by lazy {
       // FakeRestaurantDealsRepository()
       RestaurantDealsRepositoryImpl()
    }

    override val storageService: StorageService by lazy {
        FirebaseStorageService()
    }

    override val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(applicationContext)
//        FakeAuthRepositoryImpl()
    }
}
