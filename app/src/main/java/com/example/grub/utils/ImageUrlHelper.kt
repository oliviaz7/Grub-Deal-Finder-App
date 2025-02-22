package com.example.grub.utils

object ImageUrlHelper {
    private const val BASE_URL =
        "https://firebasestorage.googleapis.com/v0/b/meowmeow-6c3ec.firebasestorage.app/o/"
    private const val FILE_PATH = "deal_images%2F"
    private const val QUERY_PARAMS = "?alt=media"

    fun getFullUrl(imageId: String?): String? {
        if (imageId == null) {
            return null
        }
        val url = "$BASE_URL${FILE_PATH + imageId.replace("/", "%2F")}$QUERY_PARAMS"
        return url
    }
}