package com.example.lokanala.ui.components

import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lokanala.ui.screen.rating.Review
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.FileProvider

@Composable
fun AddEditReviewSheetContent(
    context: Context,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String, photoUris: List<Uri>) -> Unit,
    existingReview: Review? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    var rating by remember { mutableStateOf(existingReview?.rating ?: 0) }
    var comment by remember { mutableStateOf(existingReview?.comment ?: "") }
    var photoUris by remember { mutableStateOf(existingReview?.photoUris ?: emptyList()) }
    var showImageOptions by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Camera launcher
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) tempCameraUri?.let { photoUris = photoUris + it }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { selectedUris ->
        if (selectedUris.isNotEmpty()) photoUris = photoUris + selectedUris
        showImageOptions = false
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
            .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colorScheme.outlineVariant)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (existingReview != null) "Edit Ulasan Anda" else "Tulis Ulasan Anda",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Rating
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                (1..5).forEach { star ->
                    IconButton(onClick = { rating = star }) {
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "$star Star",
                            tint = if (star <= rating) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Comment
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = { Text("Tuliskan pengalaman Anda...") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary.copy(alpha = 0.7f),
                    unfocusedBorderColor = colorScheme.outlineVariant,
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurfaceVariant,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(Modifier.height(24.dp))

            // Photo preview
            if (photoUris.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(photoUris) { uri ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Foto Review",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { photoUris = photoUris - uri },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(28.dp)
                                    .background(colorScheme.surface.copy(alpha = 0.8f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus Foto", tint = colorScheme.error)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tambah foto
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { showImageOptions = true }
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Add Photo", tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                    }
                    Text("Tambahkan foto", color = colorScheme.onSurfaceVariant, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            // Submit button
            Button(
                onClick = { onSubmit(rating, comment, photoUris) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                enabled = rating > 0 && comment.isNotBlank()
            ) {
                Text(
                    text = if (existingReview != null) "Perbarui Ulasan" else "Kirim Ulasan",
                    color = colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Send, contentDescription = "Send", tint = colorScheme.onPrimary)
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Dialog sumber foto
    if (showImageOptions) {
        AlertDialog(
            onDismissRequest = { showImageOptions = false },
            title = { Text("Pilih Sumber Foto", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            showImageOptions = false
                            val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
                            Text("Ambil Foto dengan Kamera")
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    TextButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                            Text("📁", modifier = Modifier.padding(end = 12.dp))
                            Text("Pilih dari Galeri")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageOptions = false }) { Text("Batal") }
            }
        )
    }
}