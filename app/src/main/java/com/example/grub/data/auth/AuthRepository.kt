package com.example.grub.data.auth

import android.content.Context
import com.example.grub.data.Result
import com.example.grub.model.User
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages the data layer related to authentication and user profile information.
 * Is the single source of truth for authentication data, functions, and user profile details
 */
interface AuthRepository {
    suspend fun checkSavedCredentials()
    suspend fun login(username: String, password: String): Result<String> // Returns token
    suspend fun logout()
    suspend fun createUserAccount(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String
    ): Result<String>
    val loggedInUser: StateFlow<User?>
    suspend fun googleSignInButton(context: Context, rawNonce: String)

    suspend fun getUserById(userId: String) : Result<User>
}
