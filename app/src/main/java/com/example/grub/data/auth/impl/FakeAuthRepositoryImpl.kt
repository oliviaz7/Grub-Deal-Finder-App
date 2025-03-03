package com.example.grub.data.auth.impl

import com.example.grub.data.Result
import com.example.grub.data.auth.AuthRepository
import com.example.grub.data.auth.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepositoryImpl : AuthRepository {

    private var loggedInUserToken: String? = null
    private val fakeUserProfile = UserProfile(
        id = "fake_user_123",
        username = "testuser",
        email = "testuser@example.com",
    )

    private val _isLoggedIn = MutableStateFlow(loggedInUserToken != null)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    override suspend fun login(username: String, password: String): Result<String> {
        // Simulate a delay to mimic network call
        delay(500)

        return if (username.isNotBlank() && password.isNotBlank()) {
            _isLoggedIn.value = true
            loggedInUserToken = "fake_token_${username.hashCode()}"
            Result.Success(loggedInUserToken!!)
        } else {
            Result.Error(Exception("Invalid username or password"))
        }
    }

    override suspend fun logout() {
        // Simulate a delay for logout
        delay(300)
        _isLoggedIn.value = false
        loggedInUserToken = null
    }

    override suspend fun getUserProfile(): Result<UserProfile> {
        // Simulate a delay to mimic network call
        delay(500)

        return if (loggedInUserToken != null) {
            Result.Success(fakeUserProfile)
        } else {
            Result.Error(Exception("User not logged in"))
        }
    }
}