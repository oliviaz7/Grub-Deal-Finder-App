package com.example.grub.data.auth.impl

import android.content.Context
import com.example.grub.data.Result
import com.example.grub.data.auth.AuthRepository
import com.example.grub.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepositoryImpl : AuthRepository {

    private var loggedInUserToken: String? = null
    private val fakeUserProfile = User(
        id = "2b485469-7fc3-4186-b4e7-b964104b2a52",
        username = "grassyg",
        firstName = "guest",
        lastName = "user",
        email = "example@gmail.com"
    )

    private val _loggedInUser = MutableStateFlow<User?>(null)
    override val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    override suspend fun login(username: String, password: String): Result<String> {
        // Simulate a delay to mimic network call
        delay(500)

        return if (username.isNotBlank() && password.isNotBlank()) {
            _loggedInUser.value = fakeUserProfile
            loggedInUserToken = "fake_token_${username.hashCode()}"
            Result.Success(loggedInUserToken!!)
        } else {
            Result.Error(Exception("Invalid username or password"))
        }
    }

    override suspend fun logout() {
        // Simulate a delay for logout
        delay(300)
        _loggedInUser.value = null
        loggedInUserToken = null
    }

    // TODO: OLIVIA FIGURE OUT WHERE TO STORE LOGGED IN STATE AND HOW WE GET IT
    // BETWEEN APP COLD BOOTS WHERE DO WE PERSIST IT?
    suspend fun getUserProfile(): Result<User> {
        // Simulate a delay to mimic network call
        delay(500)

        return if (loggedInUserToken != null) {
            Result.Success(fakeUserProfile)
        } else {
            Result.Error(Exception("User not logged in"))
        }
    }

    override suspend fun googleSignInButton(context: Context, rawNonce: String) {
        login("username", "password")
    }
}