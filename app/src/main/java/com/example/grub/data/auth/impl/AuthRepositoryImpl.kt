package com.example.grub.data.auth.impl

import android.util.Log
import android.widget.Toast
import com.example.grub.data.auth.AuthRepository
import com.example.grub.model.User
import com.example.grub.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.grub.service.RetrofitClient.apiService
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.update
import java.security.MessageDigest

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User?
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class ChangePasswordRequest(
    val username: String,
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

class AuthRepositoryImpl(private val applicationContext: Context) : AuthRepository {
    private val _loggedInUser = MutableStateFlow<User?>(null)
    override val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    override suspend fun googleSignInButton(context: Context, rawNonce: String) {
            val credentialManager = CredentialManager.create(context)

            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("", { str, it -> str + "%02x".format(it) })

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId("337271635901-53e9n1oenq2gfjhgvhcbbk0ue4l1969q.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(true)
                .setAutoSelectEnabled(true)
                .setNonce(hashedNonce)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                Log.w("GoogleSignInButton", "Google ID Token: $googleIdToken")

                Toast.makeText(context, "You are signed in YIPPIE!!", Toast.LENGTH_SHORT).show()
            } catch (e: GetCredentialException) {
                Toast.makeText(context, e.message + ": Credential Problem", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(context, e.message + ": GoogleIdTaken Problem", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override suspend fun checkSavedCredentials() {
        val sharedPref = applicationContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)
        val password = sharedPref.getString("password", null)

        if (username != null && password != null) {
            login(username, password)
        }
    }

    private fun saveCredentials(username: String, password: String) {
        val sharedPref = applicationContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("username", username)
            putString("password", password)
            apply()
        }
    }

    override suspend fun login(username: String, password: String): Result<String> {
        val loginRequest = LoginRequest(username, password)

        val response = apiService.login(loginRequest)

        if (response.success) {
            // 1. update the loggedInUser internal state
            val user = User(
                id = response.user?.id ?: "",
                username = username,
                firstName = response.user?.firstName ?: "",
                lastName = response.user?.lastName ?: "",
                email = response.user?.email ?: "",
                upvote = response.user?.upvote ?: 0,
                downvote = response.user?.downvote ?: 0,
            )
            _loggedInUser.update { user }

            // 2. save the username and password in Android shared preferences (to persist)
            saveCredentials(username, password)

            return Result.Success(response.message)
        } else {
            return Result.Error(Exception(response.message))
        }
    }

    private fun clearCredentials() {
        val sharedPref = applicationContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    override suspend fun logout() {
        _loggedInUser.value = null
        clearCredentials()
    }

    override suspend fun createUserAccount(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String
    ): Result<String> {
        // THIS IS SO JANk fix olivia
        val response = apiService.createNewUserAccount(username, password, firstName, lastName, email)
        if (response.success) {
            val user = User(
                id = response.message, // SO BAD FIX TODO:
                username = username,
                firstName = firstName,
                lastName = lastName,
                email = email
            )
            _loggedInUser.update { user }
            return Result.Success(response.message)
        } else {
            return Result.Error(Exception(response.message))
        }
    }

    override suspend fun getUserById(userId: String): Result<User> {
        val response = apiService.getUserById(userId)
        if (response.success) {
            response.user?.let {
                val user = User(
                    id = it.id,
                    username = it.username,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                    upvote = it.upvote,
                    downvote = it.downvote,
                )
                return Result.Success(user)
            } ?: return Result.Error(Exception("User not found"))
        } else {
            return Result.Error(Exception(response.message))
        }
    }

    override suspend fun changePassword(
        username: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<String> {
        try {
            // Create a ChangePasswordRequest object
            val changePasswordRequest = ChangePasswordRequest(
                username = username,
                oldPassword = oldPassword,
                newPassword = newPassword,
                confirmPassword = confirmPassword
            )

            // Make an API call to change the password
            val response = apiService.changePassword(changePasswordRequest)

            return if (response.success) {
                // Update logged-in user with new password information
                _loggedInUser.update { user ->
                    user?.copy(
                        username = username)
                }

                // Update the stored credentials with the new password
                saveCredentials(username, newPassword)

                // Return success
                Result.Success(response.message)
            } else {
                // Handle error if API response is unsuccessful
                Result.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            // Handle any exceptions during the process
            return Result.Error(Exception("Error: Unable to change password", e))
        }
    }
}
