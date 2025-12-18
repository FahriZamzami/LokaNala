package com.example.lokanala.ui.screen.my_merchant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.lokanala.util.CategoryOrderManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response.MerchantData
import com.example.lokanala.ui.components.MyMenuItemCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.model.Product

val PromoGreenBg = Color(0xFFE8F5E9)

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun MyMerchantScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    umkmId: Int,
    viewModel: MyMerchantViewModel = viewModel()
) {
    // 1. Collect Data dari ViewModel
    val products by viewModel.products.collectAsState()
    val merchantInfo by viewModel.merchantInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    
    // State untuk konfirmasi delete
    var productToDelete by remember { mutableStateOf<com.example.lokanala.model.Product?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Ambil urutan kategori yang disimpan (reactive)
    val context = LocalContext.current
    var categoryOrder by remember(umkmId) {
        mutableStateOf(CategoryOrderManager.getCategoryOrder(context, umkmId))
    }
    
    // 2. Fetch Data saat Layar Dibuka
    LaunchedEffect(umkmId) {
        if (umkmId != 0) {
            viewModel.fetchMerchantProducts(umkmId)
            // Sinkronisasi urutan dari backend saat pertama kali load
            CategoryOrderManager.syncCategoryOrderFromBackend(
                context = context,
                umkmId = umkmId,
                onSuccess = { orderMap ->
                    categoryOrder = orderMap
                },
                onError = { errorMsg ->
                    // Jika gagal, gunakan urutan dari local storage
                    categoryOrder = CategoryOrderManager.getCategoryOrder(context, umkmId)
                }
            )
        }
    }
    
    // Buat map dari nama kategori ke categoryId (dari produk)
    val categoryIdMap = remember(products) {
        products.mapNotNull { product ->
            product.categoryName?.let { name ->
                // Ambil categoryId dari productCategoryMap jika ada
                val categoryId = viewModel.getProductCategoryId(product.id)
                if (categoryId != null && categoryId > 0) {
                    name to categoryId
                } else null
            }
        }.toMap()
    }
    
    // Grouping Produk untuk Sticky Header dengan urutan yang disimpan
    val productGroups = remember(products, categoryOrder, categoryIdMap, selectedCategory, sortOrder) {
        // Hanya grouping saat "Semua" dipilih dan bukan mode Terpopuler
        if (selectedCategory == null && sortOrder != SortOrder.RATING_DESC) {
            val grouped = products.groupBy { it.categoryName ?: "Tanpa Kategori" }
            
            // Sort kategori berdasarkan urutan yang disimpan
            if (categoryOrder.isNotEmpty() && categoryIdMap.isNotEmpty()) {
                val sortedKeys = grouped.keys.sortedBy { categoryName ->
                    val categoryId = categoryIdMap[categoryName]
                    categoryOrder[categoryId] ?: Int.MAX_VALUE
                }
                sortedKeys.associateWith { grouped[it] ?: emptyList() }
            } else {
                grouped
            }
        } else {
            // Tidak grouping saat kategori spesifik dipilih atau Terpopuler aktif
            mapOf("" to products)
        }
    }

    // Ambil semua kategori dari API (bukan dari produk yang sudah di-filter)
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
    
    // Ambil nama kategori dari API untuk dropdown (semua kategori, tidak ter-filter)
    val availableCategories = remember(categoriesState.value) {
        categoriesState.value.map { it.name }.distinct()
    }

    val colorScheme = MaterialTheme.colorScheme
    var isFabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- MENU FAB YANG MUNCUL SAAT DIKLIK ---
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. TOMBOL ATUR KATEGORI (Dipindahkan ke sini)
                        ExtendedFloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.ManageCategory.createRoute(umkmId))
                                isFabExpanded = false
                            },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            icon = { Icon(Icons.Filled.Category, contentDescription = "Kategori") },
                            text = { Text("Atur Kategori") }
                        )

                        // 2. TOMBOL TAMBAH PRODUK
                        ExtendedFloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.AddProduct.createRoute(umkmId))
                                isFabExpanded = false
                            },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            icon = { Icon(Icons.Filled.AddShoppingCart, contentDescription = "Tambah Produk") },
                            text = { Text("Tambah Produk") }
                        )
                    }
                }

                // --- TOMBOL FAB UTAMA (TOGGLE) ---
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    AnimatedContent(targetState = isFabExpanded, label = "") { expanded ->
                        if (expanded) {
                            Icon(Icons.Default.Close, "Tutup")
                        } else {
                            Icon(Icons.Filled.Add, "Tambah")
                        }
                    }
                }
            }
        },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                // --- SECTION 1: HEADER ---
                item {
                    MerchantHeader(
                        onBack = { navController.popBackStack() },
                        navController = navController,
                        umkmId = umkmId,
                        merchantData = merchantInfo
                    )
                }

                // --- SECTION 2: PROMO ---
                item {
                    PromoSection(modifier = Modifier.padding(horizontal = 16.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = colorScheme.surfaceVariant, thickness = 8.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- SECTION 3: SEARCH & FILTER (Tanpa Tombol Kategori) ---
                item {
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
                        onSortBestSelling = viewModel::onSortBestSelling,
                        onTogglePriceSort = { viewModel.togglePriceSort() },
                        onTogglePopularSort = { viewModel.togglePopularSort() }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // --- SECTION 4: CONTENT LIST ---
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (errorMessage != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage ?: "Terjadi kesalahan",
                                color = colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (productGroups.isEmpty()) {
                    item {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Produk tidak ditemukan" else "Belum ada produk",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            textAlign = TextAlign.Center,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    productGroups.forEach { (categoryName, productsInCategory) ->
                        // Sticky Header hanya muncul saat "Semua" dipilih
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
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        items(
                            items = productsInCategory,
                            key = { product -> 
                                // Create unique key by combining category, product ID, and object identity
                                // System.identityHashCode ensures uniqueness even with duplicate IDs
                                val categoryKey = categoryName ?: "uncategorized"
                                val identityHash = System.identityHashCode(product)
                                "product_${categoryKey}_${product.id}_$identityHash"
                            }
                        ) { product ->
                            MyMenuItemCard(
                                product = product,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onEditClick = { 
                                    navController.navigate(Screen.EditProduct.createRoute(umkmId, product.id))
                                },
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
        
        // Dialog Konfirmasi Delete Product
        if (showDeleteConfirmation && productToDelete != null) {
            val product = productToDelete!!
            AlertDialog(
                onDismissRequest = { 
                    showDeleteConfirmation = false
                    productToDelete = null
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = colorScheme.error,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = { 
                    Text(
                        text = "Hapus Produk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorScheme.error
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Informasi Produk
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.errorContainer.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = colorScheme.error.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Produk yang akan dihapus:",
                                    fontSize = 13.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = product.name ?: "Nama tidak tersedia",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = colorScheme.error
                                )
                                if (!product.description.isNullOrBlank()) {
                                    Text(
                                        text = product.description ?: "",
                                        fontSize = 12.sp,
                                        color = colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                                if (product.price != null) {
                                    Text(
                                        text = product.price ?: "",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = colorScheme.primary
                                    )
                                }
                            }
                        }
                        
                        // Warning Box
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = 2.dp,
                                color = colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = colorScheme.error,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Peringatan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = colorScheme.error
                                    )
                                }
                                
                                Divider(
                                    color = colorScheme.error.copy(alpha = 0.3f),
                                    thickness = 1.dp
                                )
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = colorScheme.onErrorContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Tindakan ini tidak dapat dibatalkan. Produk akan dihapus secara permanen dari sistem.",
                                        fontSize = 13.sp,
                                        color = colorScheme.onErrorContainer,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            productToDelete?.let { 
                                viewModel.deleteProduct(it)
                            }
                            showDeleteConfirmation = false
                            productToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.error
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Ya, Hapus",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showDeleteConfirmation = false
                            productToDelete = null
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Batal")
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = colorScheme.surface
            )
        }
    }
}
// ==========================================
// COMPONENT: HEADER DINAMIS
// ==========================================
@Composable
private fun MerchantHeader(
    onBack: () -> Unit,
    navController: NavController,
    umkmId: Int,
    merchantData: MerchantData?
) {
    val colorScheme = MaterialTheme.colorScheme

    val nama = merchantData?.nama ?: "Memuat..."
    val alamat = merchantData?.alamat ?: "Alamat tidak tersedia"
    val rating = merchantData?.rating ?: 0.0
    val headerImage = merchantData?.linkLokasi
    val logoImage = merchantData?.linkLokasi
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        // GAMBAR HEADER - Loading state putih, fallback logo_lokanala
        if (merchantData == null) {
            // Loading state - warna putih
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White)
            )
        } else {
            val headerUrl = headerImage?.takeIf { it.isNotBlank() }
            if (headerUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(headerUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.logo_lokanala),
                    error = painterResource(R.drawable.logo_lokanala),
                    fallback = painterResource(R.drawable.logo_lokanala),
                    contentDescription = "Header Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                // Jika data loaded tapi image null/blank - pakai logo_lokanala
                AsyncImage(
                    model = R.drawable.logo_lokanala,
                    contentDescription = "Header Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 30.dp, start = 8.dp)
                .clip(CircleShape)
                .background(colorScheme.background.copy(alpha = 0.5f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = colorScheme.primary)
        }

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
                    // LOGO TOKO - Loading state putih, fallback logo_lokanala
                    if (merchantData == null) {
                        // Loading state - warna putih
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    } else {
                        val logoUrl = logoImage?.takeIf { it.isNotBlank() }
                        if (logoUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(logoUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.logo_lokanala),
                                error = painterResource(R.drawable.logo_lokanala),
                                fallback = painterResource(R.drawable.logo_lokanala),
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            // Jika data loaded tapi image null/blank - pakai logo_lokanala
                            AsyncImage(
                                model = R.drawable.logo_lokanala,
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = nama,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format("%.1f", rating),
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
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = alamat,
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("detailscreen/$umkmId") },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Lihat Tampilan Publik",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// COMPONENT: PROMO SECTION (Static)
// ==========================================
@Composable
private fun PromoSection(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "PROMO AKTIF",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PromoGreenBg),
                onClick = { /* No action */ }
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Paket Hemat Spesial",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Rp 15.000",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ==========================================
// COMPONENT: SEARCH & FILTER (Clean, Tanpa Tombol Kategori)
// ==========================================
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
    onSortBestSelling: () -> Unit,
    onTogglePriceSort: () -> Unit,
    onTogglePopularSort: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari Produk...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
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
                        onCategorySelected(null) // Reset dropdown (akan reset Terpopuler di ViewModel)
                    }
                )
            }
            
            item {
                // Dropdown Kategori (ukuran sama dengan filter lainnya)
                CategoryDropdown(
                    modifier = Modifier,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }
            
            item {
                // Filter Terpopuler (Toggle: NONE -> RATING_DESC -> NONE)
                FilterChip(
                    text = "Terpopuler",
                    isSelected = sortOrder == SortOrder.RATING_DESC,
                    onClick = onTogglePopularSort
                )
            }
            
            item {
                // Filter Harga (Toggle: NONE -> PRICE_ASC -> PRICE_DESC -> NONE)
                FilterChip(
                    text = "Harga",
                    isSelected = sortOrder == SortOrder.PRICE_ASC || sortOrder == SortOrder.PRICE_DESC,
                    onClick = onTogglePriceSort,
                    trailingIcon = {
                        when (sortOrder) {
                            SortOrder.PRICE_ASC -> Icon(Icons.Default.ArrowUpward, null, modifier = Modifier.size(18.dp))
                            SortOrder.PRICE_DESC -> Icon(Icons.Default.ArrowDownward, null, modifier = Modifier.size(18.dp))
                            else -> Icon(Icons.Default.UnfoldMore, null, modifier = Modifier.size(18.dp))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryDropdown(
    modifier: Modifier = Modifier,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    
    Box(modifier = modifier.alpha(if (enabled) 1f else 0.5f)) {
        // Gunakan FilterChip untuk konsistensi ukuran dengan filter lainnya
        FilterChip(
            selected = selectedCategory != null && enabled,
            onClick = { 
                if (enabled) {
                    // Jika kategori sudah dipilih, langsung buka dropdown untuk ganti kategori
                    // Jika belum ada kategori yang dipilih, toggle dropdown
                    if (selectedCategory != null) {
                        expanded = true
                    } else {
                        expanded = !expanded
                    }
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
                    imageVector = Icons.Default.ArrowDropDown,
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
        
        // Dropdown Menu (menampilkan semua kategori termasuk yang sudah dipilih)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text, fontWeight = FontWeight.SemiBold) },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outlineVariant
        ),
        trailingIcon = trailingIcon
    )
}

