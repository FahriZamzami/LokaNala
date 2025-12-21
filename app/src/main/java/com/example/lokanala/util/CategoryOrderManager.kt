package com.example.lokanala.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.lokanala.data.remote.response_and_request.CategoryItem
import com.example.lokanala.data.remote.response_and_request.CategoryOrderItem
import com.example.lokanala.data.remote.response_and_request.UpdateCategoryOrderRequest
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CategoryOrderManager {
    private const val TAG = "CategoryOrderManager"
    private const val PREFS_NAME = "category_order_prefs"
    private const val KEY_PREFIX = "category_order_umkm_"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Simpan urutan kategori untuk UMKM tertentu
     * @param context Context aplikasi
     * @param umkmId ID UMKM
     * @param categoryOrder Map dari categoryId ke order index (0-based)
     */
    fun saveCategoryOrder(context: Context, umkmId: Int, categoryOrder: Map<Int, Int>) {
        val prefs = getSharedPreferences(context)
        val gson = Gson()
        val json = gson.toJson(categoryOrder)
        prefs.edit().putString("${KEY_PREFIX}$umkmId", json).apply()
    }

    /**
     * Ambil urutan kategori untuk UMKM tertentu
     * @param context Context aplikasi
     * @param umkmId ID UMKM
     * @return Map dari categoryId ke order index, atau empty map jika belum ada
     */
    fun getCategoryOrder(context: Context, umkmId: Int): Map<Int, Int> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString("${KEY_PREFIX}$umkmId", null) ?: return emptyMap()
        return try {
            val gson = Gson()
            val type = object : TypeToken<Map<Int, Int>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * Hapus urutan kategori untuk UMKM tertentu
     * @param context Context aplikasi
     * @param umkmId ID UMKM
     */
    fun clearCategoryOrder(context: Context, umkmId: Int) {
        val prefs = getSharedPreferences(context)
        prefs.edit().remove("${KEY_PREFIX}$umkmId").apply()
    }

    /**
     * Sort kategori berdasarkan urutan yang disimpan
     * @param categories List kategori yang akan diurutkan
     * @param categoryOrder Map urutan (categoryId -> order index)
     * @return List kategori yang sudah diurutkan
     */
    fun sortCategoriesByOrder(
        categories: List<com.example.lokanala.data.remote.response_and_request.CategoryItem>,
        categoryOrder: Map<Int, Int>
    ): List<com.example.lokanala.data.remote.response_and_request.CategoryItem> {
        // Jika kategori sudah memiliki field urutan dari backend, gunakan itu
        // Jika tidak, gunakan urutan dari local storage
        return if (categories.isNotEmpty() && categories.first().urutan >= 0) {
            // Kategori dari backend sudah terurut berdasarkan urutan
            categories.sortedBy { it.urutan }
        } else {
            // Fallback ke local storage order
            if (categoryOrder.isEmpty()) return categories
            categories.sortedBy { category ->
                categoryOrder[category.id] ?: Int.MAX_VALUE // Kategori tanpa urutan akan di akhir
            }
        }
    }

    /**
     * Sort kategori berdasarkan urutan yang disimpan (untuk nama kategori)
     * @param categoryNames List nama kategori yang akan diurutkan
     * @param categoryOrder Map urutan (categoryId -> order index)
     * @param categoryIdMap Map dari nama kategori ke categoryId
     * @return List nama kategori yang sudah diurutkan
     */
    fun sortCategoryNamesByOrder(
        categoryNames: List<String>,
        categoryOrder: Map<Int, Int>,
        categoryIdMap: Map<String, Int>
    ): List<String> {
        if (categoryOrder.isEmpty()) return categoryNames

        return categoryNames.sortedBy { categoryName ->
            val categoryId = categoryIdMap[categoryName]
            categoryOrder[categoryId] ?: Int.MAX_VALUE
        }
    }

    /**
     * Sinkronisasi urutan kategori ke backend
     * @param context Context aplikasi
     * @param umkmId ID UMKM
     * @param categories List kategori dengan urutan baru
     * @param onSuccess Callback saat berhasil
     * @param onError Callback saat error
     */
    fun syncCategoryOrderToBackend(
        context: Context,
        umkmId: Int,
        categories: List<CategoryItem>,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        // Buat request body dari list kategori
        val orderItems = categories.mapIndexed { index, category ->
            CategoryOrderItem(
                idKategoriProduk = category.id,
                urutan = category.urutan
            )
        }

        val request = UpdateCategoryOrderRequest(urutan = orderItems)

        // Kirim ke backend di background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.updateCategoryOrder(umkmId, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d(TAG, "Urutan kategori berhasil disinkronisasi ke backend")
                    // Simpan juga ke local sebagai backup
                    val orderMap = categories.associate { it.id to it.urutan }
                    saveCategoryOrder(context, umkmId, orderMap)
                    CoroutineScope(Dispatchers.Main).launch {
                        onSuccess()
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal menyinkronisasi urutan kategori"
                    Log.e(TAG, "Error syncing category order: $errorMsg")
                    CoroutineScope(Dispatchers.Main).launch {
                        onError(errorMsg)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception syncing category order", e)
                // Simpan ke local sebagai fallback
                val orderMap = categories.associate { it.id to it.urutan }
                saveCategoryOrder(context, umkmId, orderMap)
                CoroutineScope(Dispatchers.Main).launch {
                    onError("Gagal menyinkronisasi: ${e.message}. Data disimpan lokal.")
                }
            }
        }
    }

    /**
     * Ambil urutan kategori dari backend dan sinkronkan dengan local
     * @param context Context aplikasi
     * @param umkmId ID UMKM
     * @param onSuccess Callback saat berhasil dengan map urutan
     * @param onError Callback saat error
     */
    fun syncCategoryOrderFromBackend(
        context: Context,
        umkmId: Int,
        onSuccess: (Map<Int, Int>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.getCategories(umkmId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val categories = response.body()?.data ?: emptyList()
                    // Buat map urutan dari response (kategori sudah terurut berdasarkan urutan)
                    val orderMap = categories.associate { it.id to it.urutan }
                    // Simpan ke local
                    saveCategoryOrder(context, umkmId, orderMap)
                    Log.d(TAG, "Urutan kategori berhasil disinkronisasi dari backend")
                    CoroutineScope(Dispatchers.Main).launch {
                        onSuccess(orderMap)
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal mengambil urutan kategori"
                    Log.e(TAG, "Error fetching category order: $errorMsg")
                    CoroutineScope(Dispatchers.Main).launch {
                        onError(errorMsg)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching category order", e)
                CoroutineScope(Dispatchers.Main).launch {
                    onError("Gagal mengambil urutan: ${e.message}")
                }
            }
        }
    }
}

