package com.example.lokanala.ui.screen.edit_merchant_product

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response.CategoryItem
import com.example.lokanala.ui.screen.add_merchant_product.AddProductViewModel
import com.example.lokanala.ui.screen.my_merchant.MyMerchantViewModel
import com.example.lokanala.util.ImageUrlHelper
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

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

    // --- State Form ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryItem?>(null) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }

    // --- State Gambar (Single) ---
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showUpdateConfirmation by remember { mutableStateOf(false) }

    // --- State ViewModel ---
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isUploadSuccess.collectAsState()
    val message by viewModel.message.collectAsState()
    val productData by viewModel.productData.collectAsState()
    val productCategoryId by viewModel.productCategoryId.collectAsState()

    // 1. Load Kategori dan Product Detail saat pertama kali buka
    LaunchedEffect(umkmId, productId) {
        viewModel.fetchCategories(umkmId)
        viewModel.fetchProductDetail(productId)
        
        // Ambil category ID dari MyMerchantViewModel sebagai fallback
        val categoryIdFromList = myMerchantViewModel.getProductCategoryId(productId)
        if (categoryIdFromList != null) {
            viewModel.setProductCategoryId(categoryIdFromList)
            Log.d("EditProductScreen", "Category ID dari list: $categoryIdFromList")
        }
    }

    // 2. Pre-populate form fields saat product data loaded
    LaunchedEffect(productData, categories, productCategoryId) {
        productData?.let { product ->
            title = product.namaProduk
            description = product.deskripsi ?: ""
            price = product.harga.toInt().toString()
            currentImageUrl = ImageUrlHelper.getFullImageUrl(product.gambarUrl)
            
            // Pre-populate category
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

    // 3. Handle Pesan & Sukses
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

    // --- Launchers ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = tempCameraUri
        if (success && uri != null) {
            selectedImageUri = uri
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
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

                // --- 1. SECTION GAMBAR ---
                Text("Foto Produk", fontWeight = FontWeight.Medium)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
                        .clickable { showSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> {
                            // Gambar baru yang dipilih
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Preview Foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        currentImageUrl != null -> {
                            // Gambar existing dari server
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(currentImageUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.logo_lokanala),
                                error = painterResource(R.drawable.logo_lokanala),
                                fallback = painterResource(R.drawable.logo_lokanala),
                                contentDescription = "Current Foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { showSourceDialog = true },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        else -> {
                            // Placeholder
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("Tambah Foto", color = colors.primary)
                            }
                        }
                    }
                }

                // --- 2. FORM INPUTS ---
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Produk") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                    label = { Text("Harga (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                // Dropdown Kategori
                ExposedDropdownMenuBox(
                    expanded = categoryMenuExpanded,
                    onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        placeholder = { Text("Pilih Kategori") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false }
                    ) {
                        if (categories.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Belum ada kategori / Loading...") },
                                onClick = { categoryMenuExpanded = false }
                            )
                        } else {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategory = category
                                        categoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 3. TOMBOL UPDATE ---
                Button(
                    onClick = {
                        if (title.isNotEmpty() && price.isNotEmpty() && selectedCategory != null) {
                            showUpdateConfirmation = true
                        } else {
                            Toast.makeText(context, "Nama, Harga, dan Kategori Wajib Diisi!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
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

    // Dialog Konfirmasi Update
    if (showUpdateConfirmation) {
        AlertDialog(
            onDismissRequest = { showUpdateConfirmation = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { 
                Text(
                    text = "Perbarui Produk",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
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
                            containerColor = colors.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Produk yang akan diperbarui:",
                                fontSize = 13.sp,
                                color = colors.onSurfaceVariant
                            )
                            Text(
                                text = title.ifEmpty { "Nama produk" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.primary
                            )
                            if (description.isNotEmpty()) {
                                Text(
                                    text = description,
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                            if (price.isNotEmpty()) {
                                val formattedPrice = try {
                                    val priceValue = price.toLongOrNull()
                                    if (priceValue != null) {
                                        NumberFormat.getNumberInstance(Locale("id", "ID")).format(priceValue)
                                    } else {
                                        price
                                    }
                                } catch (e: Exception) {
                                    price
                                }
                                Text(
                                    text = "Rp $formattedPrice",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = colors.secondary
                                )
                            }
                            selectedCategory?.let { category ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Kategori:",
                                        fontSize = 12.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                    Text(
                                        text = category.name,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = colors.primary
                                    )
                                }
                            }
                        }
                    }
                    
                    // Info Box
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surfaceVariant.copy(alpha = 0.5f)
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
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Perubahan akan diterapkan pada produk ini. Pastikan semua informasi sudah benar sebelum melanjutkan.",
                                fontSize = 13.sp,
                                color = colors.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ya, Perbarui",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUpdateConfirmation = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = colors.surface
        )
    }

    // --- DIALOG SUMBER GAMBAR ---
    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Pilih Foto") },
            text = { Text("Ambil dari kamera atau galeri?") },
            confirmButton = {
                TextButton(onClick = {
                    showSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Kamera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galeri") }
            }
        )
    }
}

// --- FUNGSI HELPER UNTUK URI KAMERA ---
fun Context.createImageFileForCamera(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", externalCacheDir)
    return FileProvider.getUriForFile(
        this,
        "${packageName}.provider",
        imageFile
    )
}

