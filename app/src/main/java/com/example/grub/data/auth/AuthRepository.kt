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
    suspend fun login(username: String, password: String): Result<String> // Returns token
    suspend fun logout()
    val loggedInUser: StateFlow<User?>
    suspend fun googleSignInButton(context: Context, rawNonce: String)
}
