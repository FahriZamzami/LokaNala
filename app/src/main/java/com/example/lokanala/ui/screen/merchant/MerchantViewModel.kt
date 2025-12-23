package com.example.lokanala.ui.screen.merchant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.remote.response_and_request.merchant.MerchantData
import com.example.lokanala.data.remote.response_and_request.merchant.MerchantProduct
import com.example.lokanala.data.remote.response_and_request.merchant.MerchantResponse
import com.example.lokanala.data.remote.response_and_request.rating.OwnerCheckResponse
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale


data class MerchantUiState(
    val isLoading: Boolean = false,
    val merchantData: MerchantData? = null,
    val errorMessage: String? = null,
)

data class UmkmUiModel(
    val umkm: MerchantData,   
    val isFollowing: Boolean = false
)

class MerchantViewModel(private val userPreference: UserPreference) : ViewModel() {
    private val _uiState = MutableStateFlow(MerchantUiState())
    val uiState: StateFlow<MerchantUiState> = _uiState.asStateFlow()

    
    private val _rawProducts = MutableStateFlow<List<Product>>(emptyList())

    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    private val _umkmFollowStatus = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val umkmFollowStatus: StateFlow<Map<Long, Boolean>> = _umkmFollowStatus.asStateFlow()

    private val _isOwner = MutableStateFlow(false)
    val isOwner: StateFlow<Boolean> = _isOwner.asStateFlow()

    
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

    fun fetchFollowStatus(umkmId: Long) {
        viewModelScope.launch {
            try {
                val user = userPreference.getUser().first()
                if (user.token.isEmpty()) {
                    Log.w("MerchantVM", "Token kosong, tidak bisa fetch follow status")
                    return@launch
                }

                Log.d("MerchantVM", "Fetching follow status UMKM $umkmId with token=${user.token.take(10)}...")

                val response = ApiClient.instance.checkFollowStatus(
                    umkmId.toInt(),
                    "Bearer ${user.token}"
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("MerchantVM", "Follow status response: $body")
                    if (body != null) {
                        _umkmFollowStatus.update { map -> map + (umkmId to body.isFollowing) }
                        Log.d("MerchantVM", "Follow status for UMKM $umkmId updated: ${body.isFollowing}")
                    }
                } else {
                    Log.w("MerchantVM", "Gagal fetch follow status: ${response.code()} ${response.message()}")
                }

            } catch (e: Exception) {
                Log.w("MerchantVM", "Exception fetching follow status for UMKM $umkmId: ${e.message}")
            }
        }
    }

    fun toggleFollow(umkmId: Long) {
        viewModelScope.launch {
            try {
                val user = userPreference.getUser().first()
                val currentStatus = _umkmFollowStatus.value[umkmId] ?: false
                Log.d("MerchantVM", "Toggling follow for UMKM $umkmId. Current status=$currentStatus")

                val response = if (currentStatus) {
                    Log.d("MerchantVM", "Sending unfollow request")
                    ApiClient.instance.unfollowUmkm(umkmId.toInt(), "Bearer ${user.token}")
                } else {
                    Log.d("MerchantVM", "Sending follow request")
                    ApiClient.instance.followUmkm(umkmId.toInt(), "Bearer ${user.token}")
                }

                if (response.isSuccessful) {
                    _umkmFollowStatus.update { map -> map + (umkmId to !currentStatus) }
                    Log.d("MerchantVM", "Follow toggle successful. New status=${!currentStatus}")
                } else {
                    Log.e("MerchantVM", "Toggle follow failed: ${response.code()} ${response.message()}")
                }

            } catch (e: Exception) {
                Log.e("MerchantVM", "Exception toggling follow for UMKM $umkmId: ${e.message}")
            }
        }
    }

    fun checkIfOwner(productId: Int) {
        viewModelScope.launch {
            try {
                
                val user = userPreference.getUser().first()
                val userId = user.idUser.toInt()

                if (userId == 0) return@launch

                
                val client = ApiClient.instance.isProductOwner(productId, userId)

                client.enqueue(object : Callback<OwnerCheckResponse> {
                    override fun onResponse(
                        call: Call<OwnerCheckResponse>,
                        response: Response<OwnerCheckResponse>
                    ) {
                        
                        if (response.isSuccessful) {
                            val body = response.body()
                            _isOwner.value = body?.isOwner ?: false
                            Log.d("MerchantVM", "Is Owner result: ${body?.isOwner}")
                        } else {
                            Log.e("MerchantVM", "Failed owner check: ${response.message()}")
                            _isOwner.value = false
                        }
                    }

                    override fun onFailure(call: Call<OwnerCheckResponse>, t: Throwable) {
                        Log.e("MerchantVM", "Error connection: ${t.message}")
                        _isOwner.value = false
                    }
                })

            } catch (e: Exception) {
                Log.e("MerchantVM", "Exception: ${e.message}")
            }
        }
    }
    
    fun loadMerchantDetail(id: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        _isOwner.value = false

        val client = ApiClient.instance.getMerchantDetail(id)
        client.enqueue(object : Callback<MerchantResponse> {
            override fun onResponse(call: Call<MerchantResponse>, response: Response<MerchantResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.data != null) {
                        _uiState.value = MerchantUiState(isLoading = false, merchantData = body.data)
                        _rawProducts.value = body.data.products?.map { mapMerchantProductToProduct(it) } ?: emptyList()

                        
                        
                        body.data.products?.firstOrNull()?.let { firstProduct ->
                            checkIfOwner(firstProduct.idProduk)
                        }
                    } else {
                        _uiState.value = MerchantUiState(isLoading = false, errorMessage = body?.message ?: "Data kosong")
                    }
                } else {
                    _uiState.value = MerchantUiState(isLoading = false, errorMessage = "Gagal memuat")
                }
            }

            override fun onFailure(call: Call<MerchantResponse>, t: Throwable) {
                _uiState.value = MerchantUiState(isLoading = false, errorMessage = t.message)
            }
        })
    }

    
    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
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

    
    private fun mapMerchantProductToProduct(merchantProduct: MerchantProduct): Product {
        val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val formattedPrice = priceFormat.format(merchantProduct.harga).replace("Rp", "Rp ").trim()

        val rawImageUrl = merchantProduct.gambarUrl?.split(",")?.firstOrNull()?.trim()

        return Product(
            id = merchantProduct.idProduk,
            name = merchantProduct.namaProduk,
            description = merchantProduct.deskripsi,
            price = formattedPrice,

            
            rating = merchantProduct.ratingProduk ?: 0.0,

            reviewCount = merchantProduct.jumlahUlasan,
            categoryName = merchantProduct.kategoriProduk,
            imageUri = rawImageUrl
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

    
    fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number).replace("Rp", "Rp ").substringBeforeLast(",00")
    }
}