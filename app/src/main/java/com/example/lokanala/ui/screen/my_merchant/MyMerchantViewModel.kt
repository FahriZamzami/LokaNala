package com.example.lokanala.ui.screen.my_merchant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response_and_request.ErrorResponse
import com.example.lokanala.data.remote.response_and_request.MerchantData
import com.example.lokanala.data.remote.response_and_request.ProductItemDto
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
    RATING_DESC  
}

class MyMerchantViewModel : ViewModel() {

    private val _merchantInfo = MutableStateFlow<MerchantData?>(null)
    val merchantInfo: StateFlow<MerchantData?> = _merchantInfo.asStateFlow()

    private val _rawProducts = MutableStateFlow<List<Product>>(emptyList())

    private val _productCategoryMap = MutableStateFlow<Map<Int, Int>>(emptyMap())

    fun getProductCategoryId(productId: Int): Int? {
        return _productCategoryMap.value[productId]
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    val products: StateFlow<List<Product>> = combine(
        _rawProducts,
        _searchQuery,
        _selectedCategory,
        _sortOrder
    ) { products, query, category, sort ->
        var filtered = products

        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.name?.contains(query, ignoreCase = true) == true }
        }

        if (category != null && sort != SortOrder.RATING_DESC) {
            filtered = filtered.filter { it.categoryName == category }
        }

        filtered = when (sort) {
            SortOrder.PRICE_ASC -> filtered.sortedBy { extractPriceValue(it.price) }
            SortOrder.PRICE_DESC -> filtered.sortedByDescending { extractPriceValue(it.price) }
            SortOrder.RATING_DESC -> {
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

    
    fun fetchMerchantProducts(umkmId: Int) {

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                
                val response = ApiClient.instance.getProductsByUmkm(umkmId)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val productDtos = responseBody.data ?: emptyList()
                        _rawProducts.value = productDtos.map { dto -> mapProductDtoToProduct(dto) }

                        val categoryMap = productDtos.associate { dto ->
                            dto.idProduk to (dto.kategoriProduk?.idKategoriProduk ?: -1)
                        }.filter { it.value > 0 }
                        _productCategoryMap.value = categoryMap
                    }
                }

                val merchantResponse = ApiClient.instance.getMerchantDetailSuspend(umkmId)
                if (merchantResponse.success) {
                    val data = merchantResponse.data
                    if (data != null) {

                        val processedData = data.copy(
                            gambar = ImageUrlHelper.getFullImageUrl(data.gambar)
                        )
                        _merchantInfo.value = processedData
                        Log.d("MyMerchantVM", "Merchant Detail Loaded: ${processedData.nama}, Image: ${processedData.gambar}")
                    }
                }

            } catch (e: Exception) {
                Log.e("MyMerchantVM", "Fetch Error: ${e.message}")
                _errorMessage.value = "Gagal memuat data merchant"
            } finally {
                _isLoading.value = false
            }
        }
    }

    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = ApiClient.instance.deleteProduct(product.id)
                if (response.isSuccessful) {
                    
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

    
    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        
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

    
    fun togglePriceSort() {
        _sortOrder.value = when (_sortOrder.value) {
            SortOrder.NONE -> SortOrder.PRICE_ASC
            SortOrder.PRICE_ASC -> SortOrder.PRICE_DESC
            SortOrder.PRICE_DESC -> SortOrder.NONE
            else -> SortOrder.NONE
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

    
    private fun mapProductDtoToProduct(dto: ProductItemDto): Product {
        val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val formattedPrice = priceFormat.format(dto.harga).replace("Rp", "Rp ").trim()

        
        val categoryName = dto.kategoriProduk?.namaKategori

        
        val imageUrl = ImageUrlHelper.getFullImageUrl(dto.gambar)

        
        if (dto.gambar != null) {
            Log.d("MyMerchantVM", "Product: ${dto.namaProduk}, Original gambar: ${dto.gambar}, Full URL: $imageUrl")
        } else {
            Log.w("MyMerchantVM", "Product: ${dto.namaProduk}, gambar is NULL")
        }

        
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
}