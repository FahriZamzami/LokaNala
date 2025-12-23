package com.example.lokanala.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response_and_request.CategoryItem
import com.example.lokanala.util.ImageUrlHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.FileProvider




@Composable
fun ProductImagePicker(
    selectedImageUri: Uri?,
    currentImageUrl: String?,
    onImageSelected: (Uri?) -> Unit,
    onShowSourceDialog: () -> Unit
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
            .clickable { onShowSourceDialog() },
        contentAlignment = Alignment.Center
    ) {
        when {
            selectedImageUri != null -> {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Preview Foto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { onImageSelected(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            currentImageUrl != null -> {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(currentImageUrl).crossfade(true).build(),
                    placeholder = painterResource(R.drawable.logo_lokanala),
                    error = painterResource(R.drawable.logo_lokanala),
                    fallback = painterResource(R.drawable.logo_lokanala),
                    contentDescription = "Current Foto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { onShowSourceDialog() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.AddAPhoto, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            else -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, null, tint = colors.primary, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Tambah Foto", color = colors.primary)
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormFields(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    categories: List<CategoryItem>,
    selectedCategory: CategoryItem?,
    onCategorySelected: (CategoryItem?) -> Unit
) {
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text("Nama Produk") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true
    )

    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text("Deskripsi") },
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(10.dp)
    )

    OutlinedTextField(
        value = price,
        onValueChange = { if (it.all { char -> char.isDigit() }) onPriceChange(it) },
        label = { Text("Harga (Rp)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true
    )

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
            modifier = Modifier.fillMaxWidth().menuAnchor(),
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
                            onCategorySelected(category)
                            categoryMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}




@Composable
fun ImageSourceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Pilih Foto") },
            text = { Text("Ambil dari kamera atau galeri?") },
            confirmButton = {
                TextButton(onClick = {
                    onDismiss()
                    onCameraClick()
                }) { Text("Kamera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                    onGalleryClick()
                }) { Text("Galeri") }
            }
        )
    }
}




@Composable
fun UpdateProductConfirmationDialog(
    showDialog: Boolean,
    title: String,
    description: String,
    price: String,
    selectedCategory: CategoryItem?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(text = "Perbarui Produk", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Produk yang akan diperbarui:", fontSize = 13.sp, color = colors.onSurfaceVariant)
                            Text(text = title.ifEmpty { "Nama produk" }, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.primary)
                            if (description.isNotEmpty()) {
                                Text(text = description, fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 2)
                            }
                            if (price.isNotEmpty()) {
                                val formattedPrice = try {
                                    val priceValue = price.toLongOrNull()
                                    if (priceValue != null) {
                                        java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(priceValue)
                                    } else price
                                } catch (e: Exception) {
                                    price
                                }
                                Text(text = "Rp $formattedPrice", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colors.secondary)
                            }
                            selectedCategory?.let { category ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = "Kategori:", fontSize = 12.sp, color = colors.onSurfaceVariant)
                                    Text(text = category.name, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = colors.primary)
                                }
                            }
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Info, null, tint = colors.primary, modifier = Modifier.size(20.dp))
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
                Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = colors.primary), shape = RoundedCornerShape(8.dp)) {
                    Text(text = "Ya, Perbarui", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = colors.surface
        )
    }
}




fun Context.createImageFileForCamera(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", externalCacheDir)
    return FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        imageFile
    )
}

