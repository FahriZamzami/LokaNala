package com.example.lokanala.ui.screen.add_merchant_product

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.lokanala.ui.screen.category.Category
import com.example.lokanala.ui.screen.category.CategoryViewModel
import com.example.lokanala.ui.screen.my_merchant.MyMerchantViewModel
import com.example.lokanala.ui.theme.LokanalaTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Fungsi helper untuk membuat file URI untuk kamera
fun Context.createImageFile(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", externalCacheDir)
    return FileProvider.getUriForFile(
        this,
        "${this.packageName}.provider", // Anda perlu menambahkan provider di Manifes jika belum
        imageFile
    )
}
// CATATAN: FileProvider di atas memerlukan setup tambahan di Manifest.
// Untuk kesederhanaan, saya akan menggunakan launcher yang sedikit berbeda. Mari kita gunakan
// cara yang lebih sederhana tanpa FileProvider yang rumit.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    umkmId: Int,
    categoryViewModel: CategoryViewModel,
    myMerchantViewModel: MyMerchantViewModel,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    // --- Launcher untuk Galeri ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // --- Launcher untuk Kamera ---
    // Buat URI sementara untuk output kamera
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            imageUri = cameraImageUri
        }
    }

    // --- Launcher untuk Izin Kamera ---
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Izin diberikan, luncurkan kamera
            val newUri = context.createImageFileForCamera()
            cameraImageUri = newUri
            cameraLauncher.launch(newUri)
        } else {
            // Izin ditolak, beri tahu pengguna (opsional)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Product", fontWeight = FontWeight.Bold, color = colors.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        },
        containerColor = colors.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("Add your title..") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Add description..") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                placeholder = { Text("Add Price (e.g., 15000)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            // --- Dropdown Kategori ---
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
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Pilih Kategori")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { categoryMenuExpanded = true },
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categoryViewModel.categories.forEach { category ->
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

            // --- Bagian Foto ---
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add your photo", fontSize = 14.sp, color = colors.onSurface, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {

                    // Tombol Kamera/Galeri
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        color = colors.primary
                    ) {
                        IconButton(onClick = { showImageSourceDialog = true }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Add Photo", tint = colors.onPrimary)
                        }
                    }

                    // Preview Gambar
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Preview Gambar",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, colors.outline, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Placeholder
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surfaceVariant)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- Tombol Aksi ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                ) { Text("Cancel") }

                Button(
                    onClick = {
                        // Aksi Simpan
                        if (title.isNotBlank() && description.isNotBlank() && price.isNotBlank() && selectedCategory != null) {
                            myMerchantViewModel.addProduct(
                                name = title,
                                description = description,
                                price = price,
                                imageUri = imageUri,
                                category = selectedCategory!!.name
                            )
                            navController.popBackStack() // Kembali ke MyMerchantScreen
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                    enabled = title.isNotBlank() && description.isNotBlank() && price.isNotBlank() && selectedCategory != null
                ) { Text("Add") }
            }
        }
    }

    // --- Dialog Pilih Sumber Gambar ---
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Gambar") },
            text = { Text("Ambil gambar dari kamera atau galeri?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Kamera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeri")
                }
            }
        )
    }
}

// --- Fungsi Helper untuk membuat URI file sementara ---
fun Context.createImageFileForCamera(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFile = File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        externalCacheDir
    )
    return FileProvider.getUriForFile(
        this,
        "com.example.lokanala.provider", // Pastikan ini sesuai dengan authority
        imageFile
    )
}

// Preview (tanpa ViewModel)
@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    LokanalaTheme {
        // Halaman AddProductScreen sekarang butuh ViewModel, jadi preview-nya akan rumit.
        // Kita bisa membuat preview palsu jika diperlukan, tapi untuk sekarang kita lewati.
        Text("Preview AddProductScreen (Memerlukan ViewModel)")
    }
}
