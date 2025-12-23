package com.example.lokanala.ui.screen.merchant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.ui.ViewModelFactory
import com.example.lokanala.ui.components.MenuItemCard
import com.example.lokanala.ui.components.MerchantPromoSection
import com.example.lokanala.ui.components.MerchantPublicHeader
import com.example.lokanala.ui.components.MerchantSearchFilter
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.screen.my_merchant.SortOrder
import com.example.lokanala.util.CategoryOrderManager

@Composable
fun MerchantScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    umkmId: Long,
) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val viewModel: MerchantViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val umkmFollowStatus by viewModel.umkmFollowStatus.collectAsState()
    val isOwner by viewModel.isOwner.collectAsState() 

    var categoryOrder by remember(umkmId) {
        mutableStateOf(CategoryOrderManager.getCategoryOrder(context, umkmId.toInt()))
    }
    
    val categoriesState = remember { mutableStateOf<List<com.example.lokanala.data.remote.response_and_request.CategoryItem>>(emptyList()) }
    LaunchedEffect(umkmId) {
        try {
            val response = com.example.lokanala.data.remote.retrofit.ApiClient.instance.getCategories(umkmId.toInt())
            if (response.isSuccessful) {
                categoriesState.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            
        }
    }

    val availableCategories = remember(products) {
        products.mapNotNull { it.categoryName }
            .distinct()
            .sorted()
    }
    
    val categoryIdMap = remember(categoriesState.value) {
        categoriesState.value.associate { it.name to it.id }
    }
    
    val productGroups = remember(products, selectedCategory, sortOrder, categoryOrder, categoryIdMap) {
        if (selectedCategory == null && sortOrder != SortOrder.RATING_DESC) {
            val grouped = products.groupBy { it.categoryName ?: "Tanpa Kategori" }
            if (categoryOrder.isNotEmpty() && categoryIdMap.isNotEmpty()) {
                val sortedKeys = grouped.keys.sortedBy { categoryName ->
                    categoryOrder[categoryIdMap[categoryName]] ?: Int.MAX_VALUE
                }
                sortedKeys.associateWith { grouped[it] ?: emptyList() }
            } else {
                grouped.toSortedMap()
            }
        } else {
            mapOf("" to products)
        }
    }

    LaunchedEffect(umkmId) {
        viewModel.loadMerchantDetail(umkmId)
        viewModel.fetchFollowStatus(umkmId)
        CategoryOrderManager.syncCategoryOrderFromBackend(
            context = context,
            umkmId = umkmId.toInt(),
            onSuccess = { categoryOrder = it },
            onError = { categoryOrder = CategoryOrderManager.getCategoryOrder(context, umkmId.toInt()) }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(colorScheme.background)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = uiState.errorMessage ?: "Terjadi kesalahan", color = colorScheme.error)
                Button(onClick = { viewModel.loadMerchantDetail(umkmId) }) {
                    Text("Coba Lagi")
                }
            }
        } else {
            val merchant = uiState.merchantData
            if (merchant != null) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        MerchantPublicHeader(
                            data = merchant,
                            onBack = { navController.popBackStack() },
                            navController = navController,
                            umkmId = umkmId,
                            isFollowing = umkmFollowStatus[umkmId] ?: false,  
                            onFollowClick = { viewModel.toggleFollow(umkmId) },
                            isOwner = isOwner
                        )
                    }
                    merchant.promos.firstOrNull()?.let { latestPromo ->
                        item {
                            MerchantPromoSection(
                                promo = latestPromo,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                navController = navController,
                                umkmId = umkmId
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        MerchantSearchFilter(
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
                    if (products.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada produk tersedia.",
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                            items(
                                items = productsInCategory,
                                key = { product ->
                                    val categoryKey = categoryName
                                    val identityHash = System.identityHashCode(product)
                                    "product_${categoryKey}_${product.id}_$identityHash"
                                }
                            ) { product ->
                                MenuItemCard(
                                    product = product,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = { navController.navigate(Screen.Detail.createRoute(product.id)) }
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}
