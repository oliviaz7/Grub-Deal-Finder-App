package com.example.grub.data.auth
import androidx.credentials.Credential
import com.example.grub.data.Result
import kotlinx.coroutines.flow.StateFlow

data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
)
/**
 * Manages the data layer related to authentication and user profile information.
 * Is the single source of truth for authentication data, functions, and user profile details
 */
interface AuthRepository {
    suspend fun login(username: String, password: String): Result<String> // Returns token
    suspend fun logout()
    suspend fun handleSignIn(credential: Credential)
    val isLoggedIn: StateFlow<Boolean>
    suspend fun getUserProfile(): Result<UserProfile>
}
