package com.example.grub.model

data class User(
    val id: String, // the uuid (don't ever need to display!)
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val upvote: Int = 0,
    val downvote: Int = 0,
)
