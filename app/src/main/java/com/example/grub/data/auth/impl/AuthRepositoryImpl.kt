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
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

import java.security.MessageDigest


class AuthRepositoryImpl : AuthRepository {
    // TODO: look into using android shared preferences to persist any tokens
    private val _loggedInUser = MutableStateFlow<User?>(null)
    override val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    private lateinit var auth: FirebaseAuth

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

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

    override suspend fun login(username: String, password: String): Result<String> {
        auth = Firebase.auth
        // Add the login logic for email/password authentication
        return Result.Success("Logged in successfully")
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}
