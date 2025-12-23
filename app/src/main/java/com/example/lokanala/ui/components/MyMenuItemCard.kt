package com.example.lokanala.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.lokanala.R
import com.example.lokanala.model.Product
import com.example.lokanala.ui.theme.StarYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMenuItemCard(
    product: Product,
    modifier: Modifier = Modifier,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            val context = LocalContext.current
            val imageUrl = product.imageUri ?: product.imageRes
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.logo_lokanala),
                error = painterResource(R.drawable.logo_lokanala),
                contentDescription = product.name ?: "Gambar Produk",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name ?: "Nama tidak tersedia",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colorScheme.onSurface
                )

                Text(
                    text = product.description ?: "Deskripsi tidak tersedia",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Text(
                    text = product.price ?: "Harga tidak tersedia",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = colorScheme.primary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = StarYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.rating ?: 0.0} (${product.reviewCount ?: 0})",
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opsi",
                        tint = colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit", color = colorScheme.onSurface) },
                        onClick = {
                            expanded = false
                            onEditClick(product)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Delete", color = colorScheme.error) },
                        onClick = {
                            expanded = false
                            onDeleteClick(product)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}