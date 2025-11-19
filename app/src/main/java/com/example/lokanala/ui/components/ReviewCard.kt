package com.example.lokanala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response.rating.Review

@Composable
fun ReviewCard(
    review: Review,
    isUserReview: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }
    var fullImageUrl by remember { mutableStateOf<String?>(null) }

    val displayName = if (isUserReview) "Anda" else review.name

    val photoUrls = review.photoUrl
        ?.split(",")
        ?.filter { it.isNotBlank() }
        ?.take(5)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // FOTO PROFIL
            AsyncImage(
                model = ImageRequest.Builder(context).data(review.profilePicUrl).crossfade(true).build(),
                placeholder = painterResource(R.drawable.logo_lokanala),
                error = painterResource(R.drawable.logo_lokanala),
                fallback = painterResource(R.drawable.logo_lokanala),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // HEADER (Nama, Tanggal, Menu)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = displayName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        Text(text = review.date, style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant, fontSize = 11.sp))
                    }

                    // Menu Edit/Hapus
                    if (isUserReview) {
                        Box {
                            IconButton(onClick = { expanded = true }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = colorScheme.onSurfaceVariant)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(colorScheme.surfaceContainer)
                            ) {
                                DropdownMenuItem(text = { Text("Edit") }, onClick = { expanded = false; onEdit() }, leadingIcon = { Icon(Icons.Default.Edit, null, tint = colorScheme.primary) })
                                DropdownMenuItem(text = { Text("Hapus", color = colorScheme.error) }, onClick = { expanded = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = colorScheme.error) })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // BINTANG
                Row {
                    repeat(5) { i ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i < review.rating) Color(0xFFFFC107) else colorScheme.outlineVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // KOMENTAR
                if (review.comment.isNotEmpty()) {
                    Text(
                        text = review.comment,
                        style = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.onSurface, lineHeight = 20.sp)
                    )
                }

                // GAMBAR ULASAN (LazyRow untuk Multi-Image)
                if (!photoUrls.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        items(photoUrls) { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(url).crossfade(true).build(),
                                contentDescription = "Foto Ulasan",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp) // Ukuran Fix (Kotak 1:1)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorScheme.surfaceVariant)
                                    .clickable {
                                        fullImageUrl = url
                                        showFullImage = true
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    // 4. DIALOG GAMBAR BESAR (ZOOM) - PERBAIKAN DI SINI
    if (showFullImage && !fullImageUrl.isNullOrEmpty()) {
        Dialog(
            onDismissRequest = { showFullImage = false },
            // TAMBAHKAN PROPERTI INI: Menonaktifkan padding default platform
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            // Perbaikan: Box ini sekarang mengisi seluruh layar yang diberikan oleh Dialog
            Box(
                modifier = Modifier
                    .fillMaxSize() // Mengisi seluruh ruang
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { showFullImage = false },
                contentAlignment = Alignment.Center
            ) {
                // Gambar yang akan ditampilkan
                AsyncImage(
                    model = fullImageUrl,
                    contentDescription = "Full Image",
                    // Gunakan FillMaxWidth dan FillMaxHeight
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Tombol Tutup
                IconButton(
                    onClick = { showFullImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}