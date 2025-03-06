package com.example.grub.data.auth.impl

import com.example.grub.data.Result
import com.example.grub.data.auth.AuthRepository
import com.example.grub.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl : AuthRepository {
    // TODO: look into using android shared preferences to persist any tokens
    private val _loggedInUser = MutableStateFlow<User?>(null)
    override val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    override suspend fun login(username: String, password: String): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }
}