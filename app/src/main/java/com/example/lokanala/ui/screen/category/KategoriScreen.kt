package com.example.lokanala.ui.screen.category

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.data.remote.response.CategoryItem
import com.example.lokanala.ui.components.AddEditCategoryDialog
import com.example.lokanala.ui.components.CategoryCard
import com.example.lokanala.ui.components.DeleteCategoryConfirmationDialog
import com.example.lokanala.ui.components.EditCategoryConfirmationDialog
import com.example.lokanala.ui.screen.my_merchant.MyMerchantViewModel
import com.example.lokanala.util.CategoryOrderManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    umkmId: Int,
    viewModel: CategoryViewModel = viewModel(),
    myMerchantViewModel: MyMerchantViewModel
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val products by myMerchantViewModel.products.collectAsState()
    val context = LocalContext.current
    
    // Ambil urutan kategori yang disimpan (reactive)
    var categoryOrder by remember(umkmId) {
        mutableStateOf(CategoryOrderManager.getCategoryOrder(context, umkmId))
    }
    
    // Sort kategori berdasarkan urutan yang disimpan
    val sortedCategories = remember(categories, categoryOrder) {
        CategoryOrderManager.sortCategoriesByOrder(categories, categoryOrder)
    }

    // State untuk Dialog Input
    var showDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryItem?>(null) } // Jika null berarti Mode ADD
    
    // State untuk konfirmasi edit
    var showEditConfirmation by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryItem?>(null) }
    
    // State untuk konfirmasi delete
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<CategoryItem?>(null) }
    
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load data saat pertama kali dibuka
    LaunchedEffect(umkmId) {
        viewModel.fetchCategories(umkmId, context)
        myMerchantViewModel.fetchMerchantProducts(umkmId)
        
        // Sinkronisasi urutan dari backend saat pertama kali load
        CategoryOrderManager.syncCategoryOrderFromBackend(
            context = context,
            umkmId = umkmId,
            onSuccess = { orderMap ->
                // Urutan sudah tersimpan di local, UI akan otomatis update
            },
            onError = { errorMsg ->
                // Jika gagal, gunakan urutan dari local storage
                Log.e("KategoriScreen", "Error syncing from backend: $errorMsg")
            }
        )
    }
    
    // Fungsi untuk menghitung jumlah produk dalam kategori
    fun getProductCountByCategory(categoryId: Int, categoryName: String): Int {
        return products.count { product ->
            myMerchantViewModel.getProductCategoryId(product.id) == categoryId ||
            product.categoryName?.equals(categoryName, ignoreCase = true) == true
        }
    }
    
    // Handle error message dengan Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Kategori") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingCategory = null // Reset ke mode Add
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kategori")
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                val message = snackbarData.visuals.message
                val isSuccess = message.contains("berhasil", ignoreCase = true)
                val isError = message.contains("gagal", ignoreCase = true) || 
                             message.contains("error", ignoreCase = true)
                
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = when {
                        isSuccess -> Color(0xFF4CAF50)
                        isError -> Color(0xFFD32F2F)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = when {
                        isSuccess || isError -> Color.White
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && categories.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                val density = LocalDensity.current
                var draggedIndex by remember { mutableStateOf<Int?>(null) }
                var draggedOffset by remember { mutableStateOf(0f) }
                var targetIndex by remember { mutableStateOf<Int?>(null) }
                var previousTargetIndex by remember { mutableStateOf<Int?>(null) }
                
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedCategories.size, key = { sortedCategories[it].id }) { index ->
                        val category = sortedCategories[index]
                        val isDragging = draggedIndex == index
                        val isTarget = targetIndex == index && targetIndex != draggedIndex
                        
                        // Hitung offset untuk item yang bukan sedang di-drag
                        // Jika ada item yang di-drag, item lain perlu bergeser untuk memberi ruang
                        val itemOffset = if (draggedIndex != null && !isDragging) {
                            val draggedIdx = draggedIndex!!
                            val itemHeight = with(density) { 88.dp.toPx() }
                            val totalOffset = draggedOffset
                            
                            // Jika item ini berada di antara posisi awal dan target dari item yang di-drag
                            when {
                                // Item yang di-drag bergerak ke bawah
                                draggedIdx < index && targetIndex != null && index <= targetIndex!! -> {
                                    // Item ini perlu bergeser ke atas
                                    with(density) { (-itemHeight).toDp() }
                                }
                                // Item yang di-drag bergerak ke atas
                                draggedIdx > index && targetIndex != null && index >= targetIndex!! -> {
                                    // Item ini perlu bergeser ke bawah
                                    with(density) { itemHeight.toDp() }
                                }
                                else -> 0.dp
                            }
                        } else {
                            0.dp
                        }
                        
                        // Track perubahan target index
                        LaunchedEffect(targetIndex) {
                            if (targetIndex != null && targetIndex != previousTargetIndex && targetIndex != draggedIndex) {
                                previousTargetIndex = targetIndex
                            }
                        }

                        CategoryCard(
                            category = category,
                            isDragging = isDragging,
                            isTarget = isTarget,
                            dragOffset = if (isDragging) {
                                with(density) { draggedOffset.toDp() }
                            } else {
                                itemOffset
                            },
                            onEdit = {
                                categoryToEdit = category
                                showEditConfirmation = true
                            },
                            onDelete = {
                                categoryToDelete = category
                                showDeleteConfirmation = true
                            },
                            onDragStart = {
                                draggedIndex = index
                                draggedOffset = 0f
                            },
                            onDragEnd = {
                                val oldIdx = draggedIndex
                                val newIdx = targetIndex
                                draggedIndex = null
                                draggedOffset = 0f
                                targetIndex = null
                                previousTargetIndex = null

                                if (oldIdx != null && newIdx != null && oldIdx != newIdx) {
                                    viewModel.moveCategory(
                                        umkmId,
                                        sortedCategories,
                                        oldIdx,
                                        newIdx,
                                        context
                                    )
                                    // Update categoryOrder untuk refresh UI setelah sinkronisasi
                                    scope.launch {
                                        delay(800) // Tunggu sinkronisasi selesai
                                        categoryOrder =
                                            CategoryOrderManager.getCategoryOrder(context, umkmId)
                                    }
                                }
                            },
                            onDrag = { dragAmount ->
                                draggedOffset += dragAmount
                                // Hitung target index berdasarkan posisi drag absolut
                                // Setiap item memiliki tinggi sekitar 80dp (card) + 8dp (spacing) = 88dp
                                val itemHeight = with(density) { 88.dp.toPx() }
                                val totalOffset = draggedOffset

                                // Hitung posisi absolut dari item yang sedang di-drag
                                // Posisi awal item di layar = index * itemHeight
                                val currentItemPosition = index * itemHeight
                                val newItemPosition = currentItemPosition + totalOffset

                                // Hitung index baru berdasarkan posisi absolut
                                // Gunakan center point dari item untuk lebih akurat
                                val newIndexFloat = (newItemPosition + itemHeight / 2) / itemHeight
                                val newTargetIndex =
                                    newIndexFloat.toInt().coerceIn(0, sortedCategories.size - 1)

                                // Update target index jika berubah dan berbeda dari index saat ini
                                if (newTargetIndex != index) {
                                    if (newTargetIndex != targetIndex) {
                                        targetIndex = newTargetIndex
                                    }
                                } else {
                                    // Jika kembali ke posisi awal, reset target
                                    if (targetIndex != null) {
                                        targetIndex = null
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog Konfirmasi Edit
    if (showEditConfirmation && categoryToEdit != null) {
        val category = categoryToEdit!!
        val productCount = getProductCountByCategory(category.id, category.name)

        EditCategoryConfirmationDialog(
            category = category,
            productCount = productCount,
            onConfirm = {
                showEditConfirmation = false
                editingCategory = category
                showDialog = true
                categoryToEdit = null
            },
            onDismiss = {
                showEditConfirmation = false
                categoryToEdit = null
            }
        )
    }
    
    // Dialog Konfirmasi Delete
    if (showDeleteConfirmation && categoryToDelete != null) {
        val category = categoryToDelete!!
        val productCount = getProductCountByCategory(category.id, category.name)

        DeleteCategoryConfirmationDialog(
            category = category,
            productCount = productCount,
            onConfirm = {
                viewModel.deleteCategoryWithProducts(umkmId, category.id, productCount)
                myMerchantViewModel.fetchMerchantProducts(umkmId) // Refresh product list
                showDeleteConfirmation = false
                categoryToDelete = null
            },
            onDismiss = {
                showDeleteConfirmation = false
                categoryToDelete = null
            }
        )
    }

    // Dialog Add/Edit
    if (showDialog) {
        AddEditCategoryDialog(
            category = editingCategory,
            onDismiss = { showDialog = false },
            onSave = { name, desc ->
                if (editingCategory == null) {
                    // Create
                    viewModel.addCategory(umkmId, name, desc)
                } else {
                    // Update
                    viewModel.updateCategory(umkmId, editingCategory!!.id, name, desc)
                }
                showDialog = false
                editingCategory = null
            }
        )
    }
}

@Composable
fun EditCategoryConfirmationDialog(
    category: CategoryItem,
    productCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = { 
            Text(
                text = "Edit Kategori",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Informasi Kategori
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Kategori yang akan diedit:",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorScheme.primary
                        )
                        if (!category.description.isNullOrBlank()) {
                            Text(
                                text = category.description,
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // Informasi Jumlah Produk
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "Jumlah Produk",
                                    fontSize = 13.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$productCount produk",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = colorScheme.secondary
                                )
                            }
                        }
                    }
                }
                
                // Warning Box
                if (productCount > 0) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.errorContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = colorScheme.error.copy(alpha = 0.5f)
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
                                    text = "Peringatan Penting",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = colorScheme.error
                                )
                            }
                            
                            Divider(color = colorScheme.error.copy(alpha = 0.3f))
                            
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
                                    text = "Kategori ini memiliki $productCount produk. Pastikan produk yang tidak sesuai dengan nama dan deskripsi kategori terbaru sudah diganti terlebih dahulu sebelum melanjutkan.",
                                    fontSize = 13.sp,
                                    color = colorScheme.onErrorContainer,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    // Info jika tidak ada produk
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Kategori ini belum memiliki produk. Anda dapat mengedit dengan aman.",
                                fontSize = 13.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Ya, Lanjutkan",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = colorScheme.surface
    )
}

@Composable
fun DeleteCategoryConfirmationDialog(
    category: CategoryItem,
    productCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    AlertDialog(
        onDismissRequest = onDismiss,
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
                text = "Hapus Kategori",
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
                // Informasi Kategori yang akan dihapus
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
                            text = "Kategori yang akan dihapus:",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorScheme.error
                        )
                        if (!category.description.isNullOrBlank()) {
                            Text(
                                text = category.description,
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // Informasi Jumlah Produk
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "Jumlah Produk",
                                    fontSize = 13.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$productCount produk",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = colorScheme.secondary
                                )
                            }
                        }
                    }
                }
                
                // Warning Box - Peringatan Keras
                if (productCount > 0) {
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
                            // Header Warning
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = colorScheme.error,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "PERINGATAN KRITIS!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colorScheme.error
                                )
                            }
                            
                            Divider(
                                color = colorScheme.error.copy(alpha = 0.5f),
                                thickness = 1.dp
                            )
                            
                            // Pesan utama
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.surface.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "SEMUA $productCount PRODUK",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = colorScheme.error
                                    )
                                    Text(
                                        text = "dalam kategori ini akan ikut terhapus secara permanen dan tidak dapat dikembalikan!",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = colorScheme.onErrorContainer,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                            
                            // Saran
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.primaryContainer.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Saran Sebelum Menghapus:",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Ganti kategori produk yang masih ingin digunakan ke kategori lain sebelum menghapus kategori ini.",
                                            fontSize = 12.sp,
                                            color = colorScheme.onPrimaryContainer,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Info jika tidak ada produk
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Kategori ini belum memiliki produk. Kategori akan dihapus tanpa mempengaruhi produk lain.",
                                fontSize = 13.sp,
                                color = colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
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
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = colorScheme.surface
    )
}

@Composable
fun CategoryCard(
    category: CategoryItem,
    isDragging: Boolean = false,
    isTarget: Boolean = false,
    dragOffset: androidx.compose.ui.unit.Dp = 0.dp,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    // Animasi smooth untuk offset
    val animatedOffset by animateDpAsState(
        targetValue = dragOffset,
        animationSpec = tween(durationMillis = if (isDragging) 0 else 300),
        label = "dragOffset"
    )
    
    // Animasi scale saat dragging
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )
    
    // Animasi alpha untuk target
    val targetAlpha by animateFloatAsState(
        targetValue = if (isTarget) 0.6f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "targetAlpha"
    )
    
    Card(
        elevation = CardDefaults.cardElevation(if (isDragging) 12.dp else if (isTarget) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDragging -> colorScheme.primaryContainer.copy(alpha = 0.7f)
                isTarget -> colorScheme.secondaryContainer.copy(alpha = 0.5f)
                else -> colorScheme.surface
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedOffset)
            .scale(scale)
            .alpha(targetAlpha)
            .pointerInput(category.id) {
                detectDragGestures(
                    onDragStart = { 
                        onDragStart() 
                    },
                    onDragEnd = { 
                        onDragEnd() 
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.y)
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Drag Handle Icon (di kiri)
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Drag untuk mengubah urutan",
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!category.description.isNullOrEmpty()) {
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditCategoryDialog(
    category: CategoryItem?,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var description by remember { mutableStateOf(category?.description ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (category == null) "Tambah Kategori" else "Edit Kategori") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Kategori") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, description)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
