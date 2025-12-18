package com.example.lokanala.ui.screen.my_merchant

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.ui.components.DeleteProductDialog
import com.example.lokanala.ui.components.MerchantHeader
import com.example.lokanala.ui.components.MyMerchantFabMenu
import com.example.lokanala.ui.components.MyMerchantPromoSection
import com.example.lokanala.ui.components.MyMerchantSearchFilter
import com.example.lokanala.ui.components.MyMenuItemCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.util.CategoryOrderManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyMerchantScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    umkmId: Int,
    viewModel: MyMerchantViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val merchantInfo by viewModel.merchantInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    
    var productToDelete by remember { mutableStateOf<com.example.lokanala.model.Product?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isFabExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var categoryOrder by remember(umkmId) {
        mutableStateOf(CategoryOrderManager.getCategoryOrder(context, umkmId))
    }
    
    LaunchedEffect(umkmId) {
        if (umkmId != 0) {
            viewModel.fetchMerchantProducts(umkmId)
            CategoryOrderManager.syncCategoryOrderFromBackend(
                context = context,
                umkmId = umkmId,
                onSuccess = { categoryOrder = it },
                onError = { categoryOrder = CategoryOrderManager.getCategoryOrder(context, umkmId) }
            )
        }
    }
    
    val categoryIdMap = remember(products) {
        products.mapNotNull { product ->
            product.categoryName?.let { name ->
                val categoryId = viewModel.getProductCategoryId(product.id)
                if (categoryId != null && categoryId > 0) name to categoryId else null
            }
        }.toMap()
    }
    
    val productGroups = remember(products, categoryOrder, categoryIdMap, selectedCategory, sortOrder) {
        if (selectedCategory == null && sortOrder != SortOrder.RATING_DESC) {
            val grouped = products.groupBy { it.categoryName ?: "Tanpa Kategori" }
            if (categoryOrder.isNotEmpty() && categoryIdMap.isNotEmpty()) {
                val sortedKeys = grouped.keys.sortedBy { categoryName ->
                    categoryOrder[categoryIdMap[categoryName]] ?: Int.MAX_VALUE
                }
                sortedKeys.associateWith { grouped[it] ?: emptyList() }
            } else {
                grouped
            }
        } else {
            mapOf("" to products)
        }
    }

    val categoriesState = remember { mutableStateOf<List<com.example.lokanala.data.remote.response.CategoryItem>>(emptyList()) }
    LaunchedEffect(umkmId) {
        try {
            val response = com.example.lokanala.data.remote.retrofit.ApiClient.instance.getCategories(umkmId)
            if (response.isSuccessful) {
                categoriesState.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            // Ignore error
        }
    }
    
    val availableCategories = remember(categoriesState.value) {
        categoriesState.value.map { it.name }.distinct()
    }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            MyMerchantFabMenu(umkmId, navController, isFabExpanded) { isFabExpanded = it }
        },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(colorScheme.background).padding(padding)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    MerchantHeader(
                        onBack = { navController.popBackStack() },
                        navController = navController,
                        umkmId = umkmId,
                        merchantData = merchantInfo
                    )
                }

                item {
                    MyMerchantPromoSection(modifier = Modifier.padding(horizontal = 16.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = colorScheme.surfaceVariant, thickness = 8.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    MyMerchantSearchFilter(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        searchQuery = searchQuery,
                        onSearchQueryChanged = viewModel::onSearchQueryChanged,
                        categories = availableCategories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.onCategorySelected(it) },
                        sortOrder = sortOrder,
                        onTogglePriceSort = { viewModel.togglePriceSort() },
                        onTogglePopularSort = { viewModel.togglePopularSort() }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (errorMessage != null) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text(text = errorMessage ?: "Terjadi kesalahan", color = colorScheme.error, textAlign = TextAlign.Center)
                        }
                    }
                } else if (productGroups.isEmpty()) {
                    item {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Produk tidak ditemukan" else "Belum ada produk",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            textAlign = TextAlign.Center,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    productGroups.forEach { (categoryName, productsInCategory) ->
                        if (selectedCategory == null && sortOrder != SortOrder.RATING_DESC && categoryName.isNotEmpty()) {
                            item(key = "header_$categoryName") {
                                Surface(modifier = Modifier.fillMaxWidth(), color = colorScheme.background) {
                                    Text(
                                        text = categoryName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        items(
                            items = productsInCategory,
                            key = { product -> 
                                val categoryKey = categoryName ?: "uncategorized"
                                val identityHash = System.identityHashCode(product)
                                "product_${categoryKey}_${product.id}_$identityHash"
                            }
                        ) { product ->
                            MyMenuItemCard(
                                product = product,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onEditClick = { navController.navigate(Screen.EditProduct.createRoute(umkmId, product.id)) },
                                onDeleteClick = { 
                                    productToDelete = product
                                    showDeleteConfirmation = true
                                }
                            )
                            HorizontalDivider(
                                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
        
        if (showDeleteConfirmation && productToDelete != null) {
            DeleteProductDialog(
                product = productToDelete!!,
                onConfirm = {
                    viewModel.deleteProduct(productToDelete!!)
                    showDeleteConfirmation = false
                    productToDelete = null
                },
                onDismiss = {
                            showDeleteConfirmation = false
                            productToDelete = null
                }
            )
        }
    }
}
