package com.example.lokanala.ui.screen.my_merchant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response.ErrorResponse
import com.example.lokanala.data.remote.response.MerchantData
import com.example.lokanala.data.remote.response.ProductItemDto
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.model.Product
import com.example.lokanala.util.ImageUrlHelper
import com.google.gson.Gson
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SortOrder {
    NONE,
    BEST_SELLING,
    PRICE_ASC,
    PRICE_DESC,
    RATING_DESC  // Terpopuler berdasarkan rating tertinggi
}

class MyMerchantViewModel : ViewModel() {

    // --- STATE UTAMA ---
    // 1. Info Toko (Header)
    private val _merchantInfo = MutableStateFlow<MerchantData?>(null)
    val merchantInfo: StateFlow<MerchantData?> = _merchantInfo.asStateFlow()

    // 2. Produk Mentah dari API
    private val _rawProducts = MutableStateFlow<List<Product>>(emptyList())
    
    // 3. Mapping Product ID ke Category ID (untuk pre-populate di edit screen)
    private val _productCategoryMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    
    fun getProductCategoryId(productId: Int): Int? {
        return _productCategoryMap.value[productId]
    }

    // 3. Status Loading & Error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- FILTER & SEARCH STATE ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    // --- LOGIKA FILTERING PRODUK (COMBINE) ---
    // Menggabungkan data produk mentah dengan query pencarian, kategori, dan sort
    val products: StateFlow<List<Product>> = combine(
        _rawProducts,
        _searchQuery,
        _selectedCategory,
        _sortOrder
    ) { products, query, category, sort ->
        var filtered = products

        // 1. Filter Pencarian
        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.name?.contains(query, ignoreCase = true) == true }
        }

        // 2. Filter Kategori (nonaktif saat Terpopuler aktif)
        if (category != null && sort != SortOrder.RATING_DESC) {
            filtered = filtered.filter { it.categoryName == category }
        }

        // 3. Sorting
        filtered = when (sort) {
            SortOrder.PRICE_ASC -> filtered.sortedBy { extractPriceValue(it.price) }
            SortOrder.PRICE_DESC -> filtered.sortedByDescending { extractPriceValue(it.price) }
            SortOrder.RATING_DESC -> {
                // Terpopuler: kombinasi rating tinggi dan banyak review
                // Sort berdasarkan: (rating * weight) + (reviewCount * weight)
                // Rating lebih penting, jadi weight rating lebih besar
                filtered.sortedByDescending { product ->
                    val rating = product.rating ?: 0.0
                    val reviewCount = product.reviewCount ?: 0
                    // Formula: rating * 0.7 + reviewCount * 0.3 (normalized)
                    // Normalize reviewCount ke skala 0-5 (asumsi max 100 review = 5)
                    val normalizedReviewCount = (reviewCount.coerceAtMost(100) / 100.0) * 5.0
                    (rating * 0.7) + (normalizedReviewCount * 0.3)
                }
            }
            // Logika best selling bisa ditambahkan jika ada field 'terjual'
            else -> filtered
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- FUNGSI API ---

    // 1. Ambil Data Produk & Info Merchant
    fun fetchMerchantProducts(umkmId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // A. Fetch Produk
                val response = ApiClient.instance.getProductsByUmkm(umkmId)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        // Map API response DTO ke Model Product
                        val productDtos = responseBody.data ?: emptyList()
                        _rawProducts.value = productDtos.map { dto ->
                            mapProductDtoToProduct(dto)
                        }
                        
                        // Update product category map untuk pre-populate di edit screen
                        val categoryMap = productDtos.associate { dto ->
                            dto.idProduk to (dto.kategoriProduk?.idKategoriProduk ?: -1)
                        }.filter { it.value > 0 } // Hanya simpan yang valid
                        _productCategoryMap.value = categoryMap
                    } else {
                        _errorMessage.value = responseBody?.message ?: "Gagal memuat produk"
                    }
                } else {
                    // Parse error message dari JSON jika ada
                    val errorBodyString = try {
                        response.errorBody()?.string() ?: ""
                    } catch (e: Exception) {
                        Log.w("MyMerchantVM", "Cannot read error body: ${e.message}")
                        ""
                    }
                    
                    Log.e("MyMerchantVM", "API Error ${response.code()}: ${response.message()}")
                    Log.e("MyMerchantVM", "Error body: $errorBodyString")
                    
                    val serverErrorMessage = try {
                        if (errorBodyString.isNotEmpty()) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                            errorResponse.getErrorMessage()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.w("MyMerchantVM", "Cannot parse error JSON: ${e.message}")
                        null
                    }
                    
                    val errorMessage = when (response.code()) {
                        500 -> {
                            serverErrorMessage?.let { 
                                "Server error: $it"
                            } ?: "Server mengalami masalah. Silakan coba lagi nanti."
                        }
                        404 -> "Endpoint tidak ditemukan. Pastikan aplikasi sudah diperbarui."
                        401, 403 -> "Akses ditolak. Silakan login ulang."
                        else -> {
                            serverErrorMessage ?: "Gagal memuat produk (${response.code()}): ${response.message()}"
                        }
                    }
                    
                    _errorMessage.value = errorMessage
                }

                // B. Fetch Detail Merchant (untuk Header) - hanya jika produk berhasil dimuat
                if (_errorMessage.value == null) {
                    try {
                        val merchantResponse = ApiClient.instance.getMerchantDetailSuspend(umkmId)
                        if (merchantResponse.success) {
                            _merchantInfo.value = merchantResponse.data
                        }
                    } catch (e: Exception) {
                        Log.w("MyMerchantVM", "Failed to fetch merchant detail: ${e.message}")
                        // Tidak set error message karena produk sudah berhasil dimuat
                    }
                }

            } catch (e: Exception) {
                Log.e("MyMerchantVM", "Fetch Error", e)
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                    e.message?.contains("timeout") == true -> 
                        "Koneksi timeout. Silakan coba lagi."
                    else -> "Terjadi kesalahan: ${e.message ?: "Unknown error"}"
                }
                _errorMessage.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 2. Hapus Produk
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = ApiClient.instance.deleteProduct(product.id)
                if (response.isSuccessful) {
                    // Hapus item dari list lokal agar UI langsung update tanpa refresh
                    _rawProducts.update { list -> list.filter { it.id != product.id } }
                } else {
                    _errorMessage.value = "Gagal menghapus produk: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- EVENT HANDLERS (Untuk UI) ---
    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }

    fun onCategorySelected(category: String?) { 
        _selectedCategory.value = category
        // Reset Terpopuler jika kategori dipilih atau "Semua" diklik (category == null)
        if (_sortOrder.value == SortOrder.RATING_DESC) {
            _sortOrder.value = SortOrder.NONE
        }
    }

    fun onSortBestSelling() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.BEST_SELLING) SortOrder.NONE else SortOrder.BEST_SELLING
    }

    fun onSortPrice(isAscending: Boolean) {
        _sortOrder.value = if (isAscending) SortOrder.PRICE_ASC else SortOrder.PRICE_DESC
    }
    
    // Toggle harga: NONE -> PRICE_ASC -> PRICE_DESC -> NONE
    fun togglePriceSort() {
        _sortOrder.value = when (_sortOrder.value) {
            SortOrder.NONE -> SortOrder.PRICE_ASC
            SortOrder.PRICE_ASC -> SortOrder.PRICE_DESC
            SortOrder.PRICE_DESC -> SortOrder.NONE
            else -> SortOrder.NONE
        }
    }
    
    // Toggle Terpopuler: NONE -> RATING_DESC -> NONE
    // Saat Terpopuler aktif, reset kategori (filter lain bisa diklik untuk mengaktifkan)
    fun togglePopularSort() {
        _sortOrder.value = when (_sortOrder.value) {
            SortOrder.NONE -> {
                // Reset kategori saat Terpopuler diaktifkan
                _selectedCategory.value = null
                SortOrder.RATING_DESC
            }
            SortOrder.RATING_DESC -> SortOrder.NONE
            else -> {
                // Reset kategori saat Terpopuler diaktifkan
                _selectedCategory.value = null
                SortOrder.RATING_DESC
            }
        }
    }

    // Helper function untuk mapping DTO ke Product model
    private fun mapProductDtoToProduct(dto: ProductItemDto): Product {
        val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val formattedPrice = priceFormat.format(dto.harga).replace("Rp", "Rp ").trim()
        
        // Extract category name from category object
        val categoryName = dto.kategoriProduk?.namaKategori
        
        // Construct full image URL using helper
        val imageUrl = ImageUrlHelper.getFullImageUrl(dto.gambar)
        
        // Log untuk debugging
        if (dto.gambar != null) {
            Log.d("MyMerchantVM", "Product: ${dto.namaProduk}, Original gambar: ${dto.gambar}, Full URL: $imageUrl")
        } else {
            Log.w("MyMerchantVM", "Product: ${dto.namaProduk}, gambar is NULL")
        }
        
        // Log rating untuk debugging
        Log.d("MyMerchantVM", "Product: ${dto.namaProduk}, Rating: ${dto.ratingRataRata}, Review Count: ${dto.jumlahUlasan}")
        
        return Product(
            id = dto.idProduk,
            name = dto.namaProduk,
            description = dto.deskripsi,
            price = formattedPrice,
            rating = dto.ratingRataRata,
            reviewCount = dto.jumlahUlasan,
            categoryName = categoryName,
            imageUri = imageUrl
        )
    }
    
    // Helper function untuk extract nilai numerik dari price string
    private fun extractPriceValue(priceString: String?): Double {
        if (priceString.isNullOrBlank()) return 0.0
        // Hapus "Rp", spasi, dan titik (separator ribuan)
        val cleanPrice = priceString
            .replace("Rp", "", ignoreCase = true)
            .replace(" ", "")
            .replace(".", "")
            .replace(",", "")
            .trim()
        return try {
            cleanPrice.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }
}