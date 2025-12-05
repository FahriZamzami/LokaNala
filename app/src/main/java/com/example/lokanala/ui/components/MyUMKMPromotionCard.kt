package com.example.lokanala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.ui.screen.promotion_umkm.Promotion

@Composable
fun MyUMKMPromotionCard(
    promotion: Promotion,
    onEdit: (Promotion) -> Unit,
    onDelete: (Promotion) -> Unit,
    onItemClick: (Promotion) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }   // 🔥 Dialog hapus
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(promotion) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // 🔥 Ikon Promo
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Discount,
                    contentDescription = "Promo Icon",
                    tint = colorScheme.onPrimary
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = promotion.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${promotion.startDate} Sampai ${promotion.endDate}",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            onEdit(promotion)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = colorScheme.primary
                            )
                        }
                    )

                    // 🔥 Memunculkan dialog konfirmasi hapus
                    DropdownMenuItem(
                        text = { Text("Hapus", color = colorScheme.error) },
                        onClick = {
                            expanded = false
                            showDeleteDialog = true
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }

    // 🔥🔥 Dialog Konfirmasi Hapus Promo
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },

            title = {
                Text(
                    "Hapus Promo?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },

            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus promo ini?",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(promotion)   // 🔥 eksekusi delete
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
}