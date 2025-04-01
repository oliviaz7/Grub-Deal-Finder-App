package com.example.grub.service

import com.example.grub.data.deals.AutoPopulateDealsResponse
import retrofit2.http.Body
import retrofit2.http.POST

data class DealImageRequestBody (
    // Property name stays "image_id" for JSON compatibility
    val image_id: String
)

interface GpuApiService {
    @POST("generate")
    suspend fun autoPopulateDealFromImage(
        @Body request: DealImageRequestBody
    ): AutoPopulateDealsResponse
}
