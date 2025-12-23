package com.example.lokanala.ui.components

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
fun CustomerPromoDetailPopup(
    promotion: Promotion,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

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

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                ) {
                    Text("Tutup", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

