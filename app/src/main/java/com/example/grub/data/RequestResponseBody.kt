package com.example.grub.data

data class DealImageRequestBody (
    // Property name stays "image_id" for JSON compatibility
    val image_id: String
)

data class HandshakeResponse(
    val gpu_status: Boolean
)
