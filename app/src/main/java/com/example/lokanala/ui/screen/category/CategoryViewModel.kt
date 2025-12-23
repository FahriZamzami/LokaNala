package com.example.lokanala.ui.screen.category

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response_and_request.*
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.util.CategoryOrderManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchCategories(umkmId: Int, context: Context? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = ApiClient.instance.getCategories(umkmId)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val fetchedCategories = responseBody.data ?: emptyList()
                        _categories.value = fetchedCategories
                        
                        
                        if (context != null) {
                            val orderMap = fetchedCategories.associate { it.id to it.urutan }
                            CategoryOrderManager.saveCategoryOrder(context, umkmId, orderMap)
                        }
                    } else {
                        _errorMessage.value = responseBody?.message ?: "Gagal mengambil data"
                    }
                } else {
                    
                    val errorBodyString = try {
                        response.errorBody()?.string() ?: ""
                    } catch (e: Exception) {
                        Log.w("CategoryViewModel", "Cannot read error body: ${e.message}")
                        ""
                    }
                    
                    Log.e("CategoryViewModel", "API Error ${response.code()}: ${response.message()}")
                    Log.e("CategoryViewModel", "Error body: $errorBodyString")
                    
                    val serverErrorMessage = try {
                        if (errorBodyString.isNotEmpty()) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                            errorResponse.getErrorMessage()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.w("CategoryViewModel", "Cannot parse error JSON: ${e.message}")
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
                            serverErrorMessage ?: "Gagal mengambil data (${response.code()}): ${response.message()}"
                        }
                    }
                    
                    _errorMessage.value = errorMessage
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Error fetching categories", e)
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

    fun addCategory(umkmId: Int, name: String, description: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = CreateCategoryRequest(umkmId, name, description)
                
                val response = ApiClient.instance.addCategory(request)
                if (response.isSuccessful) {
                    fetchCategories(umkmId)
                } else {
                    _errorMessage.value = "Gagal menambah kategori"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCategory(umkmId: Int, categoryId: Int, name: String, description: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = UpdateCategoryRequest(name, description)
                
                val response = ApiClient.instance.updateCategory(categoryId, request)
                if (response.isSuccessful) {
                    fetchCategories(umkmId)
                } else {
                    _errorMessage.value = "Gagal update kategori"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCategory(umkmId: Int, categoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                
                val response = ApiClient.instance.deleteCategory(categoryId)
                if (response.isSuccessful) {
                    fetchCategories(umkmId)
                    _errorMessage.value = "Kategori berhasil dihapus"
                } else {
                    _errorMessage.value = "Gagal hapus. Mungkin kategori sedang dipakai?"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun deleteCategoryWithProducts(umkmId: Int, categoryId: Int, productCount: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                if (productCount > 0) {

                    val productsResponse = ApiClient.instance.getProductsByUmkm(umkmId)
                    if (productsResponse.isSuccessful) {
                        val products = productsResponse.body()?.data ?: emptyList()
                        val productsToDelete = products.filter {
                            it.kategoriProduk?.idKategoriProduk == categoryId
                        }


                        var deletedCount = 0
                        for (product in productsToDelete) {
                            try {
                                val deleteResponse = ApiClient.instance.deleteProduct(product.idProduk)
                                if (deleteResponse.isSuccessful) {
                                    deletedCount++
                                }
                            } catch (e: Exception) {

                            }
                        }


                        val categoryResponse = ApiClient.instance.deleteCategory(categoryId)
                        if (categoryResponse.isSuccessful) {
                            fetchCategories(umkmId)
                            _errorMessage.value = "Kategori dan $deletedCount produk berhasil dihapus"
                        } else {
                            _errorMessage.value = "Produk berhasil dihapus, tapi gagal menghapus kategori: ${categoryResponse.message()}"
                        }
                    } else {

                        val categoryResponse = ApiClient.instance.deleteCategory(categoryId)
                        if (categoryResponse.isSuccessful) {
                            fetchCategories(umkmId)
                            _errorMessage.value = "Kategori berhasil dihapus. Produk mungkin masih ada di database."
                        } else {
                            _errorMessage.value = "Gagal menghapus kategori: ${categoryResponse.message()}"
                        }
                    }
                } else {

                    val response = ApiClient.instance.deleteCategory(categoryId)
                    if (response.isSuccessful) {
                        fetchCategories(umkmId)
                        _errorMessage.value = "Kategori berhasil dihapus"
                    } else {
                        _errorMessage.value = "Gagal menghapus kategori: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Pindahkan kategori dari oldIndex ke newIndex (untuk drag and drop)
     */
    fun moveCategory(umkmId: Int, categories: List<CategoryItem>, oldIndex: Int, newIndex: Int, context: Context) {
        if (oldIndex == newIndex || oldIndex < 0 || newIndex < 0 || 
            oldIndex >= categories.size || newIndex >= categories.size) return
        
        
        val newCategoriesList = categories.toMutableList()
        val movedCategory = newCategoriesList.removeAt(oldIndex)
        newCategoriesList.add(newIndex, movedCategory)
        
        
        val updatedCategories = newCategoriesList.mapIndexed { index, category ->
            category.copy(urutan = index)
        }
        
        
        CategoryOrderManager.syncCategoryOrderToBackend(
            context = context,
            umkmId = umkmId,
            categories = updatedCategories,
            onSuccess = {
                Log.d("CategoryViewModel", "Urutan kategori berhasil disinkronisasi ke backend")
                
                fetchCategories(umkmId, context)
            },
            onError = { errorMsg ->
                Log.e("CategoryViewModel", "Error syncing category order: $errorMsg")
                _errorMessage.value = "Gagal menyinkronisasi urutan: $errorMsg"
                
                fetchCategories(umkmId, context)
            }
        )
    }

    /**
     * Inisialisasi urutan kategori (dipanggil saat pertama kali fetch)
     */
    fun initializeCategoryOrder(umkmId: Int, context: Context) {
        val currentOrder = CategoryOrderManager.getCategoryOrder(context, umkmId)
        if (currentOrder.isEmpty() && _categories.value.isNotEmpty()) {
            
            val defaultOrder = _categories.value.mapIndexed { index, category ->
                category.id to index
            }.toMap()
            CategoryOrderManager.saveCategoryOrder(context, umkmId, defaultOrder)
        }
    }
}