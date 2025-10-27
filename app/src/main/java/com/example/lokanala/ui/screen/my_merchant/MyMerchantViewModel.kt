package com.example.lokanala.ui.screen.my_merchant

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.lokanala.model.Product
import com.example.lokanala.model.dummyProducts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MyMerchantUiState(
    val products: List<Product> = emptyList()
)

class MyMerchantViewModel : ViewModel() {

    // Gunakan mutableStateListOf atau MutableStateFlow jika ingin lebih sesuai dengan Flow,
    // Di sini saya gunakan MutableStateFlow<List<Product>> untuk state management yang lebih baik.
    private val _products = MutableStateFlow(dummyProducts.toMutableList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        // Logika inisialisasi, bisa juga menerima ID UMKM untuk memfilter
    }

    fun deleteProduct(productId: Int) {
        _products.update { currentList ->
            currentList.filter { it.id != productId }.toMutableList()
        }
    }

    fun updateProduct(updatedProduct: Product) {
        _products.update { currentList ->
            val index = currentList.indexOfFirst { it.id == updatedProduct.id }
            if (index != -1) {
                currentList[index] = updatedProduct
            }
            currentList
        }
    }

    // Fungsi placeholder untuk Edit
    fun onEditProduct(product: Product, navigateToEdit: (Int) -> Unit) {
        // Di sini Anda akan menjalankan navigasi ke halaman edit produk
        // Untuk demo, kita langsung navigasi.
        navigateToEdit(product.id)
    }
}