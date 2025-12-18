package com.example.lokanala.util

import android.util.Log

object ImageUrlHelper {
    private const val BASE_URL = "https://9l45jg26-3000.asse.devtunnels.ms"
    private const val TAG = "ImageUrlHelper"
    
    /**
     * Constructs full image URL from backend response
     * Handles various formats: full URL, absolute path, relative path
     * 
     * Backend biasanya mengembalikan:
     * - Full URL: "https://..."
     * - Absolute path: "/uploads/images/file.jpg" atau "/api/uploads/images/file.jpg"
     * - Relative path: "uploads/images/file.jpg" atau "api/uploads/images/file.jpg"
     */
    fun getFullImageUrl(imagePath: String?): String? {
        if (imagePath.isNullOrBlank()) {
            Log.d(TAG, "Image path is null or blank")
            return null
        }
        
        // Trim whitespace
        val trimmedPath = imagePath.trim()
        
        val result = when {
            // Already a full URL
            trimmedPath.startsWith("http://") || trimmedPath.startsWith("https://") -> {
                // Jika URL mengandung localhost, ganti dengan devtunnel URL
                if (trimmedPath.contains("localhost") || trimmedPath.contains("127.0.0.1")) {
                    val filename = trimmedPath.substringAfterLast("/")
                    "$BASE_URL/uploads/$filename"
                } else {
                    trimmedPath
                }
            }
            // Absolute path (starts with /)
            trimmedPath.startsWith("/") -> {
                // Handle both "/uploads/..." and "/api/uploads/..."
                "$BASE_URL$trimmedPath"
            }
            // Relative path - handle various formats
            else -> {
                // Remove leading "api/" if present
                var cleanPath = trimmedPath.removePrefix("api/")
                // Remove leading slash if present
                cleanPath = cleanPath.removePrefix("/")
                
                // Try common upload paths
                val possiblePaths = listOf(
                    "/uploads/$cleanPath",
                    "/api/uploads/$cleanPath",
                    "/$cleanPath"
                )
                
                // Return first valid path (we'll let the server handle 404)
                val finalPath = possiblePaths.firstOrNull() ?: "/$cleanPath"
                "$BASE_URL$finalPath"
            }
        }
        
        Log.d(TAG, "Original: '$imagePath' -> Full URL: '$result'")
        return result
    }
}

