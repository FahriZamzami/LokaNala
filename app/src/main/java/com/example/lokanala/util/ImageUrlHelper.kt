package com.example.lokanala.util

import android.util.Log

object ImageUrlHelper {
    private const val BASE_URL = "https://9l45jg26-3000.asse.devtunnels.ms"
    private const val TAG = "ImageUrlHelper"
    fun getFullImageUrl(imagePath: String?): String? {
        if (imagePath.isNullOrBlank()) {
            Log.d(TAG, "Image path is null or blank")
            return null
        }

        val trimmedPath = imagePath.trim()
        
        val result = when {
            trimmedPath.startsWith("http://") || trimmedPath.startsWith("https://") -> {
                if (trimmedPath.contains("localhost") || trimmedPath.contains("127.0.0.1")) {
                    val filename = trimmedPath.substringAfterLast("/")
                    "$BASE_URL/uploads/$filename"
                } else {
                    trimmedPath
                }
            }
            trimmedPath.startsWith("/") -> {
                "$BASE_URL$trimmedPath"
            }
            else -> {
                var cleanPath = trimmedPath.removePrefix("api/")
                cleanPath = cleanPath.removePrefix("/")

                val possiblePaths = listOf(
                    "/uploads/$cleanPath",
                    "/api/uploads/$cleanPath",
                    "/$cleanPath"
                )

                val finalPath = possiblePaths.firstOrNull() ?: "/$cleanPath"
                "$BASE_URL$finalPath"
            }
        }
        
        Log.d(TAG, "Original: '$imagePath' -> Full URL: '$result'")
        return result
    }
}

