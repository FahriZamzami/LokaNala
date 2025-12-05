package com.example.lokanala.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lokanala.ui.screen.promotion_umkm.Promotion

@Composable
fun UMKMPromotionDetailPopup(
    promotion: Promotion,
    onDismiss: () -> Unit,
    onEdit: (Promotion) -> Unit,
    onDelete: (Promotion) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    // 🔥 State untuk dialog hapus
    var showDeleteDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 32.dp)
                .clip(RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant,
                contentColor = colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🔹 Ikon Promo
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Discount,
                        contentDescription = "Promo Icon",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = promotion.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurface
                )

                Text(
                    text = "${promotion.startDate} Sampai ${promotion.endDate}",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Text("Detail", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                Spacer(Modifier.height(8.dp))

                Text(
                    text = promotion.detail,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                Text("Syarat Penggunaan", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                Spacer(Modifier.height(8.dp))

                Text(
                    text = promotion.syarat ?: "Tidak ada syarat penggunaan",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                Text("Cara Penggunaan", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                Spacer(Modifier.height(8.dp))

                Text(
                    text = promotion.cara ?: "Tidak ada cara penggunaan",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 🔥 Tombol Hapus → membuka dialog konfirmasi
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.error
                        ),
                        border = BorderStroke(1.dp, colorScheme.error)
                    ) {
                        Text("Hapus", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            onEdit(promotion)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text("Edit", fontWeight = FontWeight.SemiBold)
                    }
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
                    lineHeight = 18.sp
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(promotion)
                        onDismiss()
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