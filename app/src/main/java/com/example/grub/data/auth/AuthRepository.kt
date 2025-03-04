package com.example.grub.data.auth
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
    val isLoggedIn: StateFlow<Boolean>
    suspend fun getUserProfile(): Result<UserProfile>
}
