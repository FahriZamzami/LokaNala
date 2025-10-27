package com.example.lokanala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.ui.screen.rating.Review

@Composable
fun AddEditReviewSheetContent(
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String, hasPhoto: Boolean) -> Unit,
    existingReview: Review? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    var rating by remember { mutableStateOf(existingReview?.rating ?: 0) }
    var comment by remember { mutableStateOf(existingReview?.comment ?: "") }
    var hasPhoto by remember { mutableStateOf(existingReview?.hasPhoto ?: false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .background(colorScheme.surface)
    ) {
        // Handle kecil di atas bottom sheet
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colorScheme.surfaceVariant)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Form utama (scrollable)
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (existingReview != null) "Edit your review" else "What is your review?",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ⭐ Rating Bar
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                (1..5).forEach { star ->
                    IconButton(onClick = { rating = star }) {
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "$star Star",
                            tint = if (star <= rating)
                                colorScheme.secondary
                            else
                                colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📝 Input komentar
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = { Text("Your review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary.copy(alpha = 0.6f),
                    unfocusedBorderColor = colorScheme.surfaceVariant,
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurfaceVariant,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 📷 Tambahkan foto (opsional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { hasPhoto = !hasPhoto }
                        .padding(end = 16.dp)
                ) {
                    IconButton(
                        onClick = { hasPhoto = !hasPhoto },
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = if (hasPhoto)
                                    colorScheme.primary
                                else
                                    colorScheme.primary.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Add Photo",
                            tint = if (hasPhoto)
                                colorScheme.onPrimary
                            else
                                colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = if (hasPhoto) "Photo added" else "Add your photo",
                        color = if (hasPhoto)
                            colorScheme.primary
                        else
                            colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 🔘 Tombol Kirim / Simpan Review
        Button(
            onClick = {
                onSubmit(rating, comment, hasPhoto)
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            Text(
                text = if (existingReview != null) "Update Review" else "Send Review",
                color = colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Default.Send,
                contentDescription = "Send",
                tint = colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
