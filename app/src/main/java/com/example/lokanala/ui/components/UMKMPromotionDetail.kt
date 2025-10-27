package com.example.lokanala.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun UMKMPromotionDetailPopup(
    promotion: Promotion,
    onDismiss: () -> Unit,
    onEdit: (Promotion) -> Unit,
    onDelete: (Promotion) -> Unit
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
                .clip(RoundedCornerShape(32.dp)),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "%",
                        color = colorScheme.primary,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Title promotion
                Text(
                    promotion.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurface
                )

                // Period promotion
                Text(
                    "${promotion.startDate} - ${promotion.endDate}",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                // Detail / Terms
                Text(
                    "Syarat dan Ketentuan:",
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    promotion.detail,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start,
                    color = colorScheme.onSurface
                )

                Spacer(Modifier.height(32.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onDelete(promotion)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.error
                        ),
                        border = BorderStroke(1.dp, colorScheme.error) // border merah
                    ) {
                        Text("Delete", fontWeight = FontWeight.SemiBold)
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
}
