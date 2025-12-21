package com.example.lokanala.util

import android.content.Context
import coil.request.ImageRequest
import coil.size.Size
import coil.size.Dimension

object ImageLoader {
    /**
     * Creates an optimized ImageRequest with size constraints to reduce memory usage
     * @param context The context
     * @param data The image URL or URI
     * @param widthDp Target width in dp (optional)
     * @param heightDp Target height in dp (optional)
     */
    fun createOptimizedRequest(
        context: Context,
        data: Any?,
        widthDp: Int? = null,
        heightDp: Int? = null
    ): ImageRequest {
        val builder = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .allowHardware(true) // Use hardware bitmaps when possible
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
        
        // Add size constraints if provided to reduce memory usage
        if (widthDp != null || heightDp != null) {
            val density = context.resources.displayMetrics.density
            val widthPx = widthDp?.let { (it * density).toInt() }
            val heightPx = heightDp?.let { (it * density).toInt() }
            
            builder.size(
                Size(
                    width = widthPx?.let { Dimension(it) } ?: Dimension.Undefined,
                    height = heightPx?.let { Dimension(it) } ?: Dimension.Undefined
                )
            )
        }
        
        return builder.build()
    }
}

