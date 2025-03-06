package com.example.grub.data.auth.impl

import android.util.Log
import com.example.grub.data.auth.AuthRepository
import com.example.grub.model.User
import com.example.grub.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.launch

class AuthRepositoryImpl : AuthRepository {
    // TODO: look into using android shared preferences to persist any tokens
    private val _loggedInUser = MutableStateFlow<User?>(null)
    override val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    private lateinit var auth: FirebaseAuth

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId("337271635901-53e9n1oenq2gfjhgvhcbbk0ue4l1969q.apps.googleusercontent.com")
        .setFilterByAuthorizedAccounts(true)
        .setAutoSelectEnabled(true)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    override suspend fun handleSignIn(credential: Credential) {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w("AuthRepository", "Credential is not of type Google ID!")
        }
    }

     private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthRepository", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.d("AuthRepository", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    // Update UI after login
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Navigate to another screen or update UI with user info
            Log.d("AuthRepository", "Logged in as: ${user.displayName}")
        } else {
            // Show error or update UI for logged out state
            Log.d("AuthRepository", "User is not logged in")
        }
    }

    override suspend fun login(username: String, password: String): Result<String> {
        auth = Firebase.auth
        // Add the login logic if needed for email/password authentication
        return Result.Success("Logged in successfully")
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}
