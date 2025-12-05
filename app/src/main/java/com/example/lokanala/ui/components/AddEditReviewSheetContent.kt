package com.example.lokanala.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.lokanala.data.remote.response_and_request.rating.Review
import java.io.File

@Composable
fun AddEditReviewSheetContent(
    context: Context,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String, photoUris: List<Uri>) -> Unit,
    existingReview: Review? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    var rating by remember { mutableIntStateOf(existingReview?.rating ?: 0) }
    var comment by remember { mutableStateOf(existingReview?.comment ?: "") }

    var photoUris by remember {
        val initialUrls = existingReview?.photoUrl
            ?.split(",") // 1. Pecah string URL (e.g., "url1,url2")
            ?.filter { it.isNotBlank() } ?: emptyList() // Filter yang kosong

        // 2. Konversi setiap URL menjadi objek Uri
        val initialUris = initialUrls.map { Uri.parse(it) }

        mutableStateOf(initialUris) // Set state dengan semua Uri
    }

    var showImageOptions by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // --- 1. SETUP UNTUK KAMERA ---
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher Kamera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraUri != null) {
            photoUris = photoUris + tempCameraUri!!
        }
    }

    // Launcher Permission (Meminta Izin Kamera)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Jika izin diberikan, langsung buka kamera
            try {
                val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
                // Authority HARUS sama dengan AndroidManifest.xml
                val authority = "com.example.lokanala.fileprovider"
                val uri = FileProvider.getUriForFile(context, authority, file)

                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Error Kamera: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher Galeri
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { selectedUris ->
        if (selectedUris.isNotEmpty()) photoUris = photoUris + selectedUris
        showImageOptions = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(scrollState)
    ) {

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (existingReview != null) "Edit Ulasan Anda" else "Tulis Ulasan Anda",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Rating Stars
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

        // Comment Input
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            placeholder = { Text("Ceritakan pengalaman Anda...") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outlineVariant,
            )
        )

        Spacer(Modifier.height(24.dp))

        // Photo Preview
        if (photoUris.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(photoUris) { uri ->
                    Box(
                        modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { photoUris = photoUris - uri },
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp).background(colorScheme.surface.copy(alpha = 0.8f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Hapus", tint = colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Button Tambah Foto
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { showImageOptions = true }
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Add", tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                }
                Text("Tambahkan foto", color = colorScheme.onSurfaceVariant, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }

        Spacer(Modifier.height(32.dp))

        // Submit Button
        Button(
            onClick = { onSubmit(rating, comment, photoUris) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            enabled = rating > 0 && comment.isNotBlank()
        ) {
            Text(text = if (existingReview != null) "Perbarui Ulasan" else "Kirim Ulasan")
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Send, contentDescription = null)
        }

        Spacer(Modifier.height(16.dp))
    }

    // DIALOG PILIHAN SUMBER FOTO
    if (showImageOptions) {
        Dialog(onDismissRequest = { showImageOptions = false }) { // Gunakan Dialog biasa
            Card( // Bungkus dengan Card agar ada elevated look
                modifier = Modifier
                    .wrapContentSize() // Penting: agar tidak full width
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .widthIn(max = 320.dp), // Batasi lebar maksimal
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pilih Foto", // Judul disingkat
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // OPSI KAMERA
                    TextButton(
                        onClick = {
                            showImageOptions = false
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Kamera",
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text("Ambil dengan Kamera") // Teks disingkat
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // OPSI GALERI
                    TextButton(
                        onClick = {
                            showImageOptions = false
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Image, // Menggunakan ikon Image
                                contentDescription = "Galeri",
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text("Pilih dari Galeri") // Teks disingkat
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Batal
                    Button(
                        onClick = { showImageOptions = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text("Batal", color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}