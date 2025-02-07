package com.example.grub.data.deals

import com.example.grub.data.Result
import com.example.grub.model.Deal

/**
 * Interface to the Deals data layer.
 */
interface DealsRepository {

    /**
     * Get deals based on .
     */
    suspend fun getDeals(): Result<List<Deal>>
}