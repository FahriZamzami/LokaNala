package com.example.lokanala.ui.screen.my_merchant

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.model.Product
import com.example.lokanala.model.dummyProducts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class SortOrder {
    NONE,
    BEST_SELLING,
    PRICE_ASC,
    PRICE_DESC
}

class MyMerchantViewModel : ViewModel() {

    private val _rawProducts = MutableStateFlow(dummyProducts)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    val products: StateFlow<List<Product>> = combine(
        _rawProducts,
        _searchQuery,
        _selectedCategory,
        _sortOrder
    ) { products, query, category, sortOrder ->
        val filteredProducts = products
            .filter { product ->
                query.isBlank() || product.name.contains(query, ignoreCase = true)
            }
            .filter { product ->
                category == null || product.categoryName == category
            }

        when (sortOrder) {
            SortOrder.BEST_SELLING -> filteredProducts.sortedByDescending { it.reviewCount }
            SortOrder.PRICE_ASC -> filteredProducts.sortedBy { it.price.filter(Char::isDigit).toIntOrNull() ?: 0 }
            SortOrder.PRICE_DESC -> filteredProducts.sortedByDescending { it.price.filter(Char::isDigit).toIntOrNull() ?: 0 }
            SortOrder.NONE -> filteredProducts
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val productGroups: StateFlow<Map<String, List<Product>>> = products.combine(_selectedCategory) { prods, category ->
        if (category != null) {
            prods.filter { it.categoryName == category }.groupBy { it.categoryName }
        } else {
            prods.groupBy { it.categoryName }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    fun onSortBestSelling() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.BEST_SELLING) SortOrder.NONE else SortOrder.BEST_SELLING
    }

    fun onSortPrice(isAscending: Boolean) {
        _sortOrder.value = if (isAscending) SortOrder.PRICE_ASC else SortOrder.PRICE_DESC
    }

    fun addProduct(name: String, description: String, price: String, imageUri: Uri?, category: String) {
        val newId = (_rawProducts.value.maxOfOrNull { it.id } ?: 0) + 1
        val newProduct = Product(
            id = newId,
            name = name,
            description = description,
            price = "Rp $price",
            rating = 0.0,
            reviewCount = 0,
            categoryName = category,
            imageUri = imageUri?.toString(),
            imageDetailUri = imageUri?.toString()
        )
        _rawProducts.update { it + newProduct }
    }

    fun deleteProduct(product: Product) {
        _rawProducts.update { currentList ->
            currentList.filter { it.id != product.id }
        }
    }

    fun updateProduct(updatedProduct: Product) {
        _rawProducts.update { currentList ->
            currentList.map { if (it.id == updatedProduct.id) updatedProduct else it }
        }
    }
}
