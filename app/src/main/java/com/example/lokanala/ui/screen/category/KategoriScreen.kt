package com.example.lokanala.ui.screen.category

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.data.remote.response_and_request.CategoryItem
import com.example.lokanala.ui.components.AddEditCategoryDialog
import com.example.lokanala.ui.components.CategoryCard
import com.example.lokanala.ui.components.CategoryConfirmationDialog
import com.example.lokanala.ui.components.CategorySnackbar
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var categoryOrder by remember(umkmId) {
        mutableStateOf(CategoryOrderManager.getCategoryOrder(context, umkmId))
    }
    val sortedCategories = remember(categories, categoryOrder) {
        CategoryOrderManager.sortCategoriesByOrder(categories, categoryOrder)
    }

    var showDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryItem?>(null) }
    var showEditConfirmation by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryItem?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<CategoryItem?>(null) }
    
    fun getProductCount(categoryId: Int, categoryName: String) = products.count {
        myMerchantViewModel.getProductCategoryId(it.id) == categoryId ||
        it.categoryName?.equals(categoryName, ignoreCase = true) == true
    }
    
    LaunchedEffect(umkmId) {
        viewModel.fetchCategories(umkmId, context)
        myMerchantViewModel.fetchMerchantProducts(umkmId)
        CategoryOrderManager.syncCategoryOrderFromBackend(
            context, umkmId, onSuccess = {}, 
            onError = { Log.e("KategoriScreen", "Error syncing: $it") }
        )
    }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editingCategory = null; showDialog = true }) {
                Icon(Icons.Default.Add, "Tambah Kategori")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { CategorySnackbar(it) } }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && categories.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                CategoryList(
                    sortedCategories = sortedCategories,
                    onEdit = { categoryToEdit = it; showEditConfirmation = true },
                    onDelete = { categoryToDelete = it; showDeleteConfirmation = true },
                    onDragEnd = { oldIdx, newIdx ->
                        if (oldIdx != null && newIdx != null && oldIdx != newIdx) {
                            viewModel.moveCategory(umkmId, sortedCategories, oldIdx, newIdx, context)
                            scope.launch {
                                delay(800)
                                categoryOrder = CategoryOrderManager.getCategoryOrder(context, umkmId)
                            }
                        }
                    }
                )
            }
        }
    }

    categoryToEdit?.let { category ->
        if (showEditConfirmation) {
            CategoryConfirmationDialog(
            category = category,
                productCount = getProductCount(category.id, category.name),
                title = "Edit Kategori",
                icon = Icons.Default.Edit,
                iconColor = MaterialTheme.colorScheme.primary,
                confirmText = "Ya, Lanjutkan",
                confirmColor = MaterialTheme.colorScheme.primary,
                isDelete = false,
            onConfirm = {
                showEditConfirmation = false
                editingCategory = category
                showDialog = true
                categoryToEdit = null
            },
                onDismiss = { showEditConfirmation = false; categoryToEdit = null }
            )
        }
    }
    
    categoryToDelete?.let { category ->
        if (showDeleteConfirmation) {
            CategoryConfirmationDialog(
            category = category,
                productCount = getProductCount(category.id, category.name),
                title = "Hapus Kategori",
                icon = Icons.Default.Delete,
                iconColor = MaterialTheme.colorScheme.error,
                confirmText = "Ya, Hapus",
                confirmColor = MaterialTheme.colorScheme.error,
                isDelete = true,
            onConfirm = {
                    viewModel.deleteCategoryWithProducts(umkmId, category.id, getProductCount(category.id, category.name))
                    myMerchantViewModel.fetchMerchantProducts(umkmId)
                showDeleteConfirmation = false
                categoryToDelete = null
            },
                onDismiss = { showDeleteConfirmation = false; categoryToDelete = null }
            )
            }
    }

    if (showDialog) {
        AddEditCategoryDialog(
            category = editingCategory,
            onDismiss = { showDialog = false },
            onSave = { name, desc ->
                if (editingCategory == null) {
                    viewModel.addCategory(umkmId, name, desc)
                } else {
                    viewModel.updateCategory(umkmId, editingCategory!!.id, name, desc)
                }
                showDialog = false
                editingCategory = null
            }
        )
    }
}

@Composable
private fun CategoryList(
    sortedCategories: List<CategoryItem>,
    onEdit: (CategoryItem) -> Unit,
    onDelete: (CategoryItem) -> Unit,
    onDragEnd: (Int?, Int?) -> Unit
) {
    val density = LocalDensity.current
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var draggedOffset by remember { mutableStateOf(0f) }
    var targetIndex by remember { mutableStateOf<Int?>(null) }
    val itemHeight = with(density) { 88.dp.toPx() }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortedCategories.size, key = { sortedCategories[it].id }) { index ->
            val category = sortedCategories[index]
            val isDragging = draggedIndex == index
            val isTarget = targetIndex == index && targetIndex != draggedIndex
            
            val itemOffset = if (draggedIndex != null && !isDragging) {
                val draggedIdx = draggedIndex!!
                when {
                    draggedIdx < index && targetIndex != null && index <= targetIndex!! -> 
                        with(density) { (-itemHeight).toDp() }
                    draggedIdx > index && targetIndex != null && index >= targetIndex!! -> 
                        with(density) { itemHeight.toDp() }
                    else -> 0.dp
                }
            } else 0.dp

            CategoryCard(
                category = category,
                isDragging = isDragging,
                isTarget = isTarget,
                dragOffset = if (isDragging) with(density) { draggedOffset.toDp() } else itemOffset,
                onEdit = { onEdit(category) },
                onDelete = { onDelete(category) },
                onDragStart = { draggedIndex = index; draggedOffset = 0f },
                onDragEnd = {
                    onDragEnd(draggedIndex, targetIndex)
                    draggedIndex = null
                    draggedOffset = 0f
                    targetIndex = null
                },
                onDrag = { dragAmount ->
                    draggedOffset += dragAmount
                    val newTarget = ((index * itemHeight + draggedOffset + itemHeight / 2) / itemHeight).toInt()
                        .coerceIn(0, sortedCategories.size - 1)
                    targetIndex = if (newTarget != index) newTarget else null
                }
            )
        }
    }
}

