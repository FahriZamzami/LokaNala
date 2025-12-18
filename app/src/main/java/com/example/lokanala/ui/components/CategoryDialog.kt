package com.example.lokanala.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.data.remote.response.CategoryItem

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
        title = { Text(if (category == null) "Tambah Kategori" else "Edit Kategori") },
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
            Button(onClick = { if (name.isNotBlank()) onSave(name, description) }) {
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

@Composable
fun CategoryConfirmationDialog(
    category: CategoryItem,
    productCount: Int,
    title: String,
    icon: ImageVector,
    iconColor: Color,
    confirmText: String,
    confirmColor: Color,
    isDelete: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp)) },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (isDelete) colorScheme.error else Color.Unspecified
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = (if (isDelete) colorScheme.errorContainer else colorScheme.primaryContainer)
                            .copy(alpha = if (isDelete) 0.2f else 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isDelete) BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.3f)) else null
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Kategori yang akan ${if (isDelete) "dihapus" else "diedit"}:",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDelete) colorScheme.error else colorScheme.primary
                        )
                        category.description?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(text = "Jumlah Produk", fontSize = 13.sp, color = colorScheme.onSurfaceVariant)
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
                
                if (productCount > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDelete) colorScheme.errorContainer
                            else colorScheme.errorContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            if (isDelete) 2.dp else 1.dp,
                            colorScheme.error.copy(alpha = if (isDelete) 1f else 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = colorScheme.error,
                                    modifier = Modifier.size(if (isDelete) 28.dp else 24.dp)
                                )
                                Text(
                                    text = if (isDelete) "PERINGATAN KRITIS!" else "Peringatan Penting",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isDelete) 16.sp else 14.sp,
                                    color = colorScheme.error
                                )
                            }
                            if (isDelete) {
                                Divider(color = colorScheme.error.copy(alpha = 0.5f), thickness = 1.dp)
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = colorScheme.surface.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = colorScheme.primaryContainer.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
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
                                text = if (isDelete) "Kategori ini belum memiliki produk. Kategori akan dihapus tanpa mempengaruhi produk lain."
                                else "Kategori ini belum memiliki produk. Anda dapat mengedit dengan aman.",
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
                colors = ButtonDefaults.buttonColors(confirmColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = confirmText,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDelete) Color.White else Color.Unspecified
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = colorScheme.surface
    )
}

@Composable
fun CategorySnackbar(snackbarData: SnackbarData) {
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
        contentColor = if (isSuccess || isError) Color.White 
                       else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(8.dp)
    )
}
