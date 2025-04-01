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

    private val fakeUserProfile2 = User(
        id = "8d425cd5-27e9-472a-aff9-7aa9a1f2f288",
        username = "angolina",
        firstName = "angolina",
        lastName = "banjolina",
        email = "ds@gmail.com"
    )

    private val _loggedInUser = MutableStateFlow<User?>(null)
    override val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()
    override suspend fun checkSavedCredentials() = Unit

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

    override suspend fun createUserAccount(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String
    ): Result<String> {
        delay(500)

        return if (username.isNotBlank() && password.isNotBlank()) {
            _loggedInUser.value = fakeUserProfile
            loggedInUserToken = "fake_token_${username.hashCode()}"
            Result.Success(loggedInUserToken!!)
        } else {
            Result.Error(Exception("Invalid username or password"))
        }
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

    override suspend fun getUserById(userId: String): Result<User> {
        return Result.Success(fakeUserProfile2)
    }
}