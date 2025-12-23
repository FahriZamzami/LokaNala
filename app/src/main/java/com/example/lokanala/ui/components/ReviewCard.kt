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
import com.example.lokanala.data.remote.response_and_request.rating.Review

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

    
    var showDeleteDialog by remember { mutableStateOf(false) }

    val displayName = if (isUserReview) "Anda" else review.name
    val photoUrls = review.photoUrl?.split(",")?.filter { it.isNotBlank() }?.take(5)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(review.profilePicUrl).crossfade(true).build(),
                placeholder = painterResource(R.drawable.logo_lokanala),
                error = painterResource(R.drawable.logo_lokanala),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(displayName, fontWeight = FontWeight.Bold)
                        Text(review.date, fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
                    }

                    if (isUserReview) {
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, null, tint = colorScheme.onSurfaceVariant)
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        expanded = false
                                        onEdit()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Edit, null, tint = colorScheme.primary) }
                                )

                                DropdownMenuItem(
                                    text = { Text("Hapus", color = colorScheme.error) },
                                    onClick = {
                                        expanded = false
                                        showDeleteDialog = true   
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = colorScheme.error) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

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

                if (review.comment.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(review.comment)
                }

                if (!photoUrls.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(photoUrls) { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(url).crossfade(true).build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = "Review Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
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

    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Ulasan?") },
            text = { Text("Apakah Anda yakin ingin menghapus ulasan ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()     
                    }
                ) {
                    Text("Hapus", color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    
    if (showFullImage && !fullImageUrl.isNullOrEmpty()) {
        Dialog(
            onDismissRequest = { showFullImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).clickable { showFullImage = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fullImageUrl,
                    contentScale = ContentScale.Fit,
                    contentDescription = "Full Image",
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { showFullImage = false },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}