package com.example.lokanala.ui.screen.merchant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.lokanala.util.CategoryOrderManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response.merchant.MerchantData
import com.example.lokanala.data.remote.response.merchant.MerchantPromo
import com.example.lokanala.ui.components.MenuItemCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.*
import com.example.lokanala.ui.screen.my_merchant.SortOrder
import com.example.lokanala.data.remote.response.CategoryItem
import com.example.lokanala.data.remote.retrofit.ApiClient

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MerchantScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    umkmId: Long,
    viewModel: MerchantViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    // Ambil urutan kategori yang disimpan (reactive)
    var categoryOrder by remember(umkmId) {
        mutableStateOf(CategoryOrderManager.getCategoryOrder(context, umkmId.toInt()))
    }
    
    // Fetch categories untuk mapping nama ke ID (ambil semua kategori dari API)
    val categoriesState = remember { mutableStateOf<List<com.example.lokanala.data.remote.response.CategoryItem>>(emptyList()) }
    LaunchedEffect(umkmId) {
        try {
            val response = com.example.lokanala.data.remote.retrofit.ApiClient.instance.getCategories(umkmId.toInt())
            if (response.isSuccessful) {
                categoriesState.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            // Ignore error
        }
    }
    
    // Ambil semua kategori dari API untuk dropdown (semua kategori, tidak ter-filter)
    val availableCategories = remember(categoriesState.value) {
        categoriesState.value.map { it.name }.distinct()
    }
    
    // Buat map dari nama kategori ke categoryId
    val categoryIdMap = remember(categoriesState.value) {
        categoriesState.value.associate { it.name to it.id }
    }
    
    // Grouping Produk per Kategori (hanya saat "Semua" dipilih)
    val productGroups = remember(products, selectedCategory, sortOrder, categoryOrder, categoryIdMap) {
        if (selectedCategory == null && sortOrder != SortOrder.RATING_DESC) {
            // Group per kategori saat "Semua" dipilih
            val grouped = products.groupBy { it.categoryName ?: "Tanpa Kategori" }
            
            // Sort kategori berdasarkan urutan yang disimpan
            if (categoryOrder.isNotEmpty() && categoryIdMap.isNotEmpty()) {
                val sortedKeys = grouped.keys.sortedBy { categoryName ->
                    val categoryId = categoryIdMap[categoryName]
                    categoryOrder[categoryId] ?: Int.MAX_VALUE
                }
                sortedKeys.associateWith { grouped[it] ?: emptyList() }
            } else {
                grouped.toSortedMap() // Fallback: urutkan secara alfabetis
            }
        } else {
            // Tidak grouping saat kategori spesifik dipilih atau Terpopuler aktif
            mapOf("" to products) // Key kosong agar tidak muncul header
        }
    }

    // 1. Panggil API saat layar pertama kali dibuka
    LaunchedEffect(umkmId) {
        viewModel.loadMerchantDetail(umkmId)
        // Sinkronisasi urutan dari backend saat pertama kali load
        CategoryOrderManager.syncCategoryOrderFromBackend(
            context = context,
            umkmId = umkmId.toInt(),
            onSuccess = { orderMap ->
                categoryOrder = orderMap
            },
            onError = { errorMsg ->
                // Jika gagal, gunakan urutan dari local storage
                categoryOrder = CategoryOrderManager.getCategoryOrder(context, umkmId.toInt())
            }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(colorScheme.background)) {
        // --- HANDLING STATE ---
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null) {
            // Tampilkan pesan error jika gagal load
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.errorMessage ?: "Terjadi kesalahan",
                    color = colorScheme.error
                )
                Button(onClick = { viewModel.loadMerchantDetail(umkmId) }) {
                    Text("Coba Lagi")
                }
            }
        } else {
            // --- DATA SUKSES DIMUAT ---
            val merchant = uiState.merchantData

            if (merchant != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Scope untuk LazyColumn dengan ExperimentalFoundationApi
                    // A. HEADER UMKM (Foto & Info)
                    item {
                        MerchantHeader(
                            data = merchant,
                            onBack = { navController.popBackStack() },
                            navController = navController,
                            umkmId = umkmId,
                            colorScheme = colorScheme
                        )
                    }

                    // B. PROMO TERBARU (Hanya muncul jika ada promo)
                    // Ambil promo pertama dari list (karena di controller sudah di sort by ID desc)
                    val latestPromo = merchant.promos.firstOrNull()

                    if (latestPromo != null) {
                        item {
                            PromoSection(
                                promo = latestPromo,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                navController = navController
                            )
                        }
                    }

                    // C. DIVIDER, SEARCH & FILTER
                    item {
                        // Beri jarak agak jauh dari promo/header
                        Spacer(modifier = Modifier.height(24.dp))

                        SearchAndFilterSection(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            searchQuery = searchQuery,
                            onSearchQueryChanged = viewModel::onSearchQueryChanged,
                            categories = availableCategories,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { category ->
                                viewModel.onCategorySelected(category)
                            },
                            sortOrder = sortOrder,
                            onTogglePriceSort = { viewModel.togglePriceSort() },
                            onTogglePopularSort = { viewModel.togglePopularSort() },
                            colorScheme = colorScheme
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    // D. LIST PRODUK (Grouped per Kategori)
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
                            // Sticky Header untuk Nama Kategori (hanya saat "Semua" dipilih)
                            if (selectedCategory == null && sortOrder != SortOrder.RATING_DESC && categoryName.isNotEmpty()) {
                                stickyHeader {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = colorScheme.background
                                    ) {
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

                            // Items per kategori
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
                                    onClick = {
                                        // Navigasi ke Detail Produk
                                        navController.navigate(Screen.Detail.createRoute(product.id))
                                    }
                                )
                            }
                        }
                    }

                    // Padding bawah extra agar item terakhir tidak tertutup navigasi
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}
// -----------------------------------------------------------------
// KOMPONEN UI
// -----------------------------------------------------------------

@Composable
private fun MerchantHeader(
    data: MerchantData,
    onBack: () -> Unit,
    navController: NavController,
    umkmId: Long,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        // 1. GAMBAR HEADER UTAMA
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.imageHeaderUrl)
                .crossfade(true)
                .build(),

            // Gambar Placeholder (Saat Loading) - bisa pakai gambar abu-abu atau logo
            placeholder = painterResource(R.drawable.logo_lokanala),

            // Gambar Error (Jika URL 404/Offline) -> Pakai Logo Lokanala
            error = painterResource(R.drawable.logo_lokanala),

            // Gambar Fallback (Jika URL dari database NULL) -> Pakai Logo Lokanala
            fallback = painterResource(R.drawable.logo_lokanala),

            contentDescription = "Header UMKM",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        // Tombol Back
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 30.dp, start = 8.dp)
                .clip(CircleShape)
                .background(colorScheme.background.copy(alpha = 0.5f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = colorScheme.primary)
        }

        // Kartu Informasi UMKM (Floating Card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Logo Kecil (Bulat) - Menggunakan logika fallback yang sama
                    AsyncImage(
                        model = data.imageHeaderUrl,
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        placeholder = painterResource(R.drawable.logo_lokanala),
                        error = painterResource(R.drawable.logo_lokanala),
                        fallback = painterResource(R.drawable.logo_lokanala)
                    )

                    Spacer(Modifier.width(16.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = data.nama,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = data.rating.toString(),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = colorScheme.secondary
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.Star,
                                "Rating",
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Kategori Label
                            Surface(
                                color = colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = data.kategori,
                                    fontSize = 10.sp,
                                    color = colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = data.alamat ?: "Alamat tidak tersedia",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Link Detail
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("detailscreen/$umkmId") },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Lihat Detail UMKM",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Lihat detail",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PromoSection(
    promo: MerchantPromo,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    // Warna Hijau Tua (Sesuai Screenshot)
    val GreenPromoColor = Color(0xFF104618)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "PROMO MENARIK",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = GreenPromoColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = {
                navController.navigate(Screen.Promo.route)
            }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Nama Promo
                Text(
                    text = promo.namaPromo.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Deskripsi Promo (Karena harga tidak ada)
                Text(
                    text = promo.deskripsi ?: "Dapatkan penawaran menarik hari ini!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Link "Lihat lainnya"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lihat lainnya",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F6F), // Warna Pink/Merah Muda
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFFFF6F6F),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFilterSection(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    sortOrder: SortOrder,
    onTogglePriceSort: () -> Unit,
    onTogglePopularSort: () -> Unit,
    colorScheme: ColorScheme
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari Produk...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = colorScheme.primary
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = colorScheme.surfaceVariant,
                focusedContainerColor = colorScheme.surfaceVariant
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // LazyRow untuk Button Semua, Dropdown Kategori, dan Filter Harga (scrollable)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                // Button Semua
                FilterChip(
                    text = "Semua",
                    isSelected = selectedCategory == null && sortOrder != SortOrder.RATING_DESC,
                    onClick = { 
                        onCategorySelected(null)
                    }
                )
            }
            
            item {
                // Dropdown Kategori
                CategoryDropdown(
                    modifier = Modifier,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }
            
            item {
                // Filter Terpopuler
                FilterChip(
                    text = "Terpopuler",
                    isSelected = sortOrder == SortOrder.RATING_DESC,
                    onClick = onTogglePopularSort
                )
            }
            
            item {
                // Filter Harga
                FilterChip(
                    text = "Harga",
                    isSelected = sortOrder == SortOrder.PRICE_ASC || sortOrder == SortOrder.PRICE_DESC,
                    onClick = onTogglePriceSort,
                    trailingIcon = {
                        when (sortOrder) {
                            SortOrder.PRICE_ASC -> Icon(Icons.Filled.ArrowUpward, null, modifier = Modifier.size(18.dp))
                            SortOrder.PRICE_DESC -> Icon(Icons.Filled.ArrowDownward, null, modifier = Modifier.size(18.dp))
                            else -> Icon(Icons.Filled.UnfoldMore, null, modifier = Modifier.size(18.dp))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text, fontWeight = FontWeight.SemiBold) },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colorScheme.surfaceVariant,
            selectedContainerColor = colorScheme.primaryContainer,
            labelColor = colorScheme.onSurface,
            selectedLabelColor = colorScheme.primary
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) colorScheme.primaryContainer else colorScheme.outlineVariant
        ),
        trailingIcon = trailingIcon
    )
}

@Composable
private fun CategoryDropdown(
    modifier: Modifier = Modifier,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    
    Box(modifier = modifier) {
        FilterChip(
            selected = selectedCategory != null,
            onClick = { 
                // Jika kategori sudah dipilih, langsung buka dropdown untuk ganti kategori
                // Jika belum ada kategori yang dipilih, toggle dropdown
                if (selectedCategory != null) {
                    expanded = true
                } else {
                    expanded = !expanded
                }
            },
            label = { 
                Text(
                    text = selectedCategory ?: "Kategori Produk",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            shape = RoundedCornerShape(8.dp),
            colors = FilterChipDefaults.filterChipColors(
                containerColor = colorScheme.surfaceVariant,
                selectedContainerColor = if (selectedCategory != null) colorScheme.primaryContainer else colorScheme.surfaceVariant,
                labelColor = colorScheme.onSurface,
                selectedLabelColor = if (selectedCategory != null) colorScheme.primary else colorScheme.onSurface
            ),
            border = BorderStroke(
                1.dp,
                if (selectedCategory != null) colorScheme.primaryContainer else colorScheme.outlineVariant
            )
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (categories.isNotEmpty()) {
                // List semua kategori (termasuk yang sudah dipilih)
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                category,
                                fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        onClick = {
                            // Jika klik kategori yang sama, deselect (reset ke null)
                            // Jika klik kategori berbeda, pilih kategori baru
                            if (selectedCategory == category) {
                                onCategorySelected(null)
                            } else {
                                onCategorySelected(category)
                            }
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = if (selectedCategory == category) colorScheme.primary else colorScheme.onSurface
                        )
                    )
                }
            } else {
                DropdownMenuItem(
                    text = { Text("Belum ada kategori", style = MaterialTheme.typography.bodySmall) },
                    onClick = { expanded = false },
                    enabled = false
                )
            }
        }
    }
}
