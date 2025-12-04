package com.example.lokanala.ui.screen.my_merchant

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Import ApiClient yang benar (pastikan nama object di file APIClient.kt adalah ApiClient)
import com.example.lokanala.data.remote.retrofit.ApiClient
// Import Model Response (Pastikan file MerchantDetailResponse.kt sudah dibuat sesuai instruksi sebelumnya)
import com.example.lokanala.data.remote.response.MerchantData
import com.example.lokanala.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

enum class SortOrder {
    NONE,
    BEST_SELLING,
    PRICE_ASC,
    PRICE_DESC
}

class MyMerchantViewModel : ViewModel() {

    // --- STATE DATA ---

    // 1. Menyimpan Info Toko untuk Header (Nama, Alamat, Rating, Foto Toko)
    private val _merchantInfo = MutableStateFlow<MerchantData?>(null)
    val merchantInfo: StateFlow<MerchantData?> = _merchantInfo.asStateFlow()

    // 2. Menyimpan List Produk Mentah dari API
    private val _rawProducts = MutableStateFlow<List<Product>>(emptyList())

    // 3. State Loading & Error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- STATE FILTER & SORTING (Client Side) ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // Menggabungkan data Produk dari API dengan logika filter & sort aplikasi
    val products: StateFlow<List<Product>> = combine(
        _rawProducts,
        _searchQuery,
        _selectedCategory,
        _sortOrder
    ) { products, query, category, sortOrder ->
        // Logic Filter (Pencarian & Kategori)
        var filteredProducts = products.filter { product ->
            (query.isBlank() || product.name.contains(query, ignoreCase = true)) &&
                    (category == null || product.categoryName == category)
        }

        // Logic Sorting (Harga)
        filteredProducts = when (sortOrder) {
            SortOrder.PRICE_ASC -> filteredProducts.sortedBy { parsePrice(it.price) }
            SortOrder.PRICE_DESC -> filteredProducts.sortedByDescending { parsePrice(it.price) }
            else -> filteredProducts
        }
        filteredProducts
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // =========================================================================
    // FUNGSI UTAMA: Fetch Data dari API
    // =========================================================================
    fun fetchMerchantProducts(umkmId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Panggil API (Pastikan getMerchantDetail ada di APIService.kt)
                val response = ApiClient.instance.getMerchantDetail(umkmId)

                if (response.success && response.data != null) {
                    // A. Update Info Toko (Header)
                    _merchantInfo.value = response.data

                    // B. Update List Produk
                    val mappedProducts = response.data.products.map { apiProduct ->
                        Product(
                            id = apiProduct.idProduk,
                            name = apiProduct.namaProduk,
                            description = apiProduct.deskripsi ?: "Tidak ada deskripsi",
                            price = formatRupiah(apiProduct.harga),
                            rating = 0.0, // Default 0.0 jika API produk belum ada rating
                            reviewCount = apiProduct.jumlahUlasan ?: 0,
                            categoryName = apiProduct.kategoriProduk ?: "Lainnya",
                            // Pastikan URL gambar valid
                            imageUri = apiProduct.gambar,
                            imageDetailUri = apiProduct.gambar
                        )
                    }
                    _rawProducts.value = mappedProducts
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                Log.e("MyMerchantVM", "Error fetch: ${e.message}")
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =========================================================================
    // HELPER FUNCTIONS
    // =========================================================================

    private fun formatRupiah(number: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(number).replace("Rp", "Rp ")
    }

    private fun parsePrice(priceString: String): Double {
        return priceString.replace(Regex("[^\\d]"), "").toDoubleOrNull() ?: 0.0
    }

    // Fungsi-fungsi UI Event Handler
    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onCategorySelected(category: String?) { _selectedCategory.value = category }
    fun onSortBestSelling() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.BEST_SELLING) SortOrder.NONE else SortOrder.BEST_SELLING
    }
    fun onSortPrice(isAscending: Boolean) {
        _sortOrder.value = if (isAscending) SortOrder.PRICE_ASC else SortOrder.PRICE_DESC
    }

    // CRUD Placeholder (Nanti hubungkan ke endpoint API POST/DELETE)
    fun addProduct(name: String, description: String, price: String, imageUri: Uri?, category: String) {
        // Implementasi API POST di sini nanti
    }

    fun deleteProduct(product: Product) {
        // Implementasi API DELETE di sini nanti

        // Hapus sementara dari list lokal agar UI responsif
        _rawProducts.update { list -> list.filter { it.id != product.id } }
    }
}