package com.example.lokanala.ui.screen.add_merchant_product

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.lokanala.data.remote.response_and_request.CategoryItem
import com.example.lokanala.data.remote.response_and_request.product.ProductDetailData
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.util.ImageUrlHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AddProductViewModel : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isUploadSuccess = MutableStateFlow(false)
    val isUploadSuccess: StateFlow<Boolean> = _isUploadSuccess
    
    
    private val _productData = MutableStateFlow<ProductDetailData?>(null)
    val productData: StateFlow<ProductDetailData?> = _productData.asStateFlow()
    
    
    private val _productCategoryId = MutableStateFlow<Int?>(null)
    val productCategoryId: StateFlow<Int?> = _productCategoryId.asStateFlow()
    
    fun setProductCategoryId(categoryId: Int?) {
        _productCategoryId.value = categoryId
    }

    fun fetchCategories(umkmId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.instance.getCategories(umkmId)
                if (response.isSuccessful) {
                    _categories.value = response.body()?.data ?: emptyList()
                } else {
                    _message.value = "Gagal memuat kategori"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    
    fun addProduct(
        context: Context,
        umkmId: Int,
        categoryId: Int,
        name: String,
        description: String,
        price: String,
        imageUri: Uri? 
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                
                val idUmkmPart = umkmId.toString().toRequestBody("text/plain".toMediaType())
                val idKatPart = categoryId.toString().toRequestBody("text/plain".toMediaType())
                val namePart = name.toRequestBody("text/plain".toMediaType())
                val descPart = description.toRequestBody("text/plain".toMediaType())
                val pricePart = price.toRequestBody("text/plain".toMediaType())

                
                var imagePart: MultipartBody.Part? = null
                if (imageUri != null) {
                    val file = uriToFile(imageUri, context)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    
                    imagePart = MultipartBody.Part.createFormData("gambar", file.name, requestFile)
                }

                
                val response = ApiClient.instance.addProduct(
                    idUmkm = idUmkmPart,
                    idKategori = idKatPart,
                    nama = namePart,
                    deskripsi = descPart,
                    harga = pricePart,
                    gambar = imagePart 
                )

                if (response.isSuccessful) {
                    _isUploadSuccess.value = true
                    _message.value = "Produk berhasil disimpan!"
                } else {
                    _message.value = "Gagal: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchProductDetail(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.instance.getProductDetailSuspend(productId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _productData.value = body.data
                        Log.d("AddProductViewModel", "Product detail loaded: ${body.data?.namaProduk}")
                    } else {
                        _message.value = "Gagal memuat data produk"
                    }
                } else {
                    _message.value = "Gagal memuat: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateProduct(
        context: Context,
        productId: Int,
        umkmId: Int,
        categoryId: Int,
        name: String,
        description: String,
        price: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                
                val idUmkmPart = umkmId.toString().toRequestBody("text/plain".toMediaType())
                val idKatPart = categoryId.toString().toRequestBody("text/plain".toMediaType())
                val namePart = name.toRequestBody("text/plain".toMediaType())
                val descPart = description.toRequestBody("text/plain".toMediaType())
                val pricePart = price.toRequestBody("text/plain".toMediaType())

                
                var imagePart: MultipartBody.Part? = null
                if (imageUri != null) {
                    val file = uriToFile(imageUri, context)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("gambar", file.name, requestFile)
                }

                
                val response = ApiClient.instance.updateProduct(
                    productId = productId,
                    idUmkm = idUmkmPart,
                    idKategori = idKatPart,
                    nama = namePart,
                    deskripsi = descPart,
                    harga = pricePart,
                    gambar = imagePart
                )

                if (response.isSuccessful) {
                    _isUploadSuccess.value = true
                    _message.value = "Produk berhasil diperbarui!"
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AddProductViewModel", "Update gagal! Code: ${response.code()}, Message: ${response.message()}")
                    Log.e("AddProductViewModel", "Error Body: $errorBody")
                    _message.value = "Gagal update: ${response.message()}\nError: $errorBody"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _isUploadSuccess.value = false
        _message.value = null
        _productData.value = null
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile("temp_upload", ".jpg", context.cacheDir)
        val inputStream = contentResolver.openInputStream(selectedImg) as java.io.InputStream
        val outputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
        return myFile
    }
}