package com.example.lokanala.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.lokanala.model.Product
import com.example.lokanala.model.Review
import com.example.lokanala.model.dummyProducts
import com.example.lokanala.model.dummyReviews
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    init {
        val productId: Int = savedStateHandle.get<Int>("productId") ?: -1
        if (productId != -1) {
            _product.value = dummyProducts.find { it.id == productId }
            // Untuk saat ini, kita akan menampilkan semua review sebagai dummy
            _reviews.value = dummyReviews
        }
    }
}
