package com.example.lokanala.ui.screen.edit_merchant_product

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.data.remote.response.CategoryItem
import com.example.lokanala.ui.components.*
import com.example.lokanala.ui.screen.add_merchant_product.AddProductViewModel
import com.example.lokanala.ui.screen.my_merchant.MyMerchantViewModel
import com.example.lokanala.util.ImageUrlHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    navController: NavController,
    umkmId: Int,
    productId: Int,
    viewModel: AddProductViewModel = viewModel(),
    myMerchantViewModel: MyMerchantViewModel
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryItem?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showUpdateConfirmation by remember { mutableStateOf(false) }

    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isUploadSuccess.collectAsState()
    val message by viewModel.message.collectAsState()
    val productData by viewModel.productData.collectAsState()
    val productCategoryId by viewModel.productCategoryId.collectAsState()

    LaunchedEffect(umkmId, productId) {
        viewModel.fetchCategories(umkmId)
        viewModel.fetchProductDetail(productId)
        val categoryIdFromList = myMerchantViewModel.getProductCategoryId(productId)
        if (categoryIdFromList != null) {
            viewModel.setProductCategoryId(categoryIdFromList)
            Log.d("EditProductScreen", "Category ID dari list: $categoryIdFromList")
        }
    }

    LaunchedEffect(productData, categories, productCategoryId) {
        productData?.let { product ->
            title = product.namaProduk
            description = product.deskripsi ?: ""
            price = product.harga.toInt().toString()
            currentImageUrl = ImageUrlHelper.getFullImageUrl(product.gambarUrl)
            val categoryId = productCategoryId
            if (categoryId != null) {
                val matchedCategory = categories.find { it.id == categoryId }
                if (matchedCategory != null) {
                    selectedCategory = matchedCategory
                    Log.d("EditProductScreen", "Category matched dari categoryId: ${matchedCategory.name}")
                }
            }
            Log.d("EditProductScreen", "Form pre-populated: title=$title, price=$price")
        }
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            if (!isSuccess) viewModel.resetState()
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = tempCameraUri
        if (success && uri != null) selectedImageUri = uri
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val uri = context.createImageFileForCamera()
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Produk", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Foto Produk", fontWeight = FontWeight.Medium)
                ProductImagePicker(
                    selectedImageUri = selectedImageUri,
                    currentImageUrl = currentImageUrl,
                    onImageSelected = { selectedImageUri = it },
                    onShowSourceDialog = { showSourceDialog = true }
                )
                ProductFormFields(
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    price = price,
                    onPriceChange = { price = it },
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (title.isNotEmpty() && price.isNotEmpty() && selectedCategory != null) {
                            showUpdateConfirmation = true
                        } else {
                            Toast.makeText(context, "Nama, Harga, dan Kategori Wajib Diisi!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Memperbarui...")
                    } else {
                        Text("Perbarui Produk")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    UpdateProductConfirmationDialog(
        showDialog = showUpdateConfirmation,
        title = title,
        description = description,
        price = price,
        selectedCategory = selectedCategory,
        onConfirm = {
            selectedCategory?.let { category ->
                showUpdateConfirmation = false
                viewModel.updateProduct(
                    context = context,
                    productId = productId,
                    umkmId = umkmId,
                    categoryId = category.id,
                    name = title,
                    description = description,
                    price = price,
                    imageUri = selectedImageUri
                )
            }
        },
        onDismiss = { showUpdateConfirmation = false }
    )

    ImageSourceDialog(
        showDialog = showSourceDialog,
        onDismiss = { showSourceDialog = false },
        onCameraClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        onGalleryClick = { galleryLauncher.launch("image/*") }
    )
}
