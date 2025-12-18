package com.example.lokanala.ui.screen.merchant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response.merchant.MerchantData
import com.example.lokanala.data.remote.response.merchant.MerchantProduct
import com.example.lokanala.data.remote.response.merchant.MerchantResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.model.Product
import com.example.lokanala.ui.screen.my_merchant.SortOrder
import com.example.lokanala.util.ImageUrlHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

// State untuk UI
data class MerchantUiState(
    val isLoading: Boolean = false,
    val merchantData: MerchantData? = null,
    val errorMessage: String? = null
)

class MerchantViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MerchantUiState())
    val uiState: StateFlow<MerchantUiState> = _uiState.asStateFlow()

    // Produk mentah dari API
    private val _rawProducts = MutableStateFlow<List<Product>>(emptyList())

    // Filter & Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    // Logika Filtering Produk (sama dengan MyMerchantViewModel)
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
                filtered.sortedByDescending { product ->
                    val rating = product.rating ?: 0.0
                    val reviewCount = product.reviewCount ?: 0
                    val normalizedReviewCount = (reviewCount.coerceAtMost(100) / 100.0) * 5.0
                    (rating * 0.7) + (normalizedReviewCount * 0.3)
                }
            }
            else -> filtered
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fungsi dipanggil saat layar dibuka
    fun loadMerchantDetail(id: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        val client = ApiClient.instance.getMerchantDetail(id)
        client.enqueue(object : Callback<MerchantResponse> {
            override fun onResponse(
                call: Call<MerchantResponse>,
                response: Response<MerchantResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _uiState.value = MerchantUiState(
                            isLoading = false,
                            merchantData = body.data
                        )
                        // Map MerchantProduct ke Product model
                        _rawProducts.value = body.data?.products?.map { mapMerchantProductToProduct(it) } ?: emptyList()
                    } else {
                        _uiState.value = MerchantUiState(
                            isLoading = false,
                            errorMessage = body?.message ?: "Data kosong"
                        )
                    }
                } else {
                    _uiState.value = MerchantUiState(
                        isLoading = false,
                        errorMessage = "Gagal memuat: ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<MerchantResponse>, t: Throwable) {
                Log.e("MerchantVM", "Error: ${t.message}")
                _uiState.value = MerchantUiState(
                    isLoading = false,
                    errorMessage = "Koneksi error: ${t.message}"
                )
            }
        })
    }

    // Event Handlers
    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }

    fun onCategorySelected(category: String?) { 
        _selectedCategory.value = category
        // Reset Terpopuler jika kategori dipilih atau "Semua" diklik
        if (_sortOrder.value == SortOrder.RATING_DESC) {
            _sortOrder.value = SortOrder.NONE
        }
    }

    fun togglePriceSort() {
        _sortOrder.value = when (_sortOrder.value) {
            SortOrder.NONE -> SortOrder.PRICE_ASC
            SortOrder.PRICE_ASC -> SortOrder.PRICE_DESC
            SortOrder.PRICE_DESC -> SortOrder.NONE
            else -> SortOrder.PRICE_ASC
        }
    }

    fun togglePopularSort() {
        _sortOrder.value = when (_sortOrder.value) {
            SortOrder.NONE -> {
                _selectedCategory.value = null
                SortOrder.RATING_DESC
            }
            SortOrder.RATING_DESC -> SortOrder.NONE
            else -> {
                _selectedCategory.value = null
                SortOrder.RATING_DESC
            }
        }
    }

    // Helper untuk mapping MerchantProduct ke Product
    private fun mapMerchantProductToProduct(merchantProduct: MerchantProduct): Product {
        val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val formattedPrice = priceFormat.format(merchantProduct.harga).replace("Rp", "Rp ").trim()
        val imageUrl = ImageUrlHelper.getFullImageUrl(merchantProduct.gambarUrl)
        
        return Product(
            id = merchantProduct.idProduk,
            name = merchantProduct.namaProduk,
            description = merchantProduct.deskripsi,
            price = formattedPrice,
            rating = null, // MerchantProduct tidak punya rating, perlu dari backend
            reviewCount = merchantProduct.jumlahUlasan,
            categoryName = merchantProduct.kategoriProduk,
            imageUri = imageUrl
        )
    }

    // Helper untuk extract nilai numerik dari price string
    private fun extractPriceValue(priceString: String?): Double {
        if (priceString.isNullOrBlank()) return 0.0
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

    // Helper untuk format Rupiah
    fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number).replace("Rp", "Rp ").substringBeforeLast(",00")
    }
}