package com.example.lokanala.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response.product.TopReviewData
import com.example.lokanala.ui.theme.LokanalaTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RatingItem(
    review: TopReviewData, // Gunakan model dari API
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FOTO PROFIL USER (COIL)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(review.fotoUserUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.logo_lokanala), // Ganti placeholder user
                error = painterResource(R.drawable.logo_lokanala),
                fallback = painterResource(R.drawable.logo_lokanala),
                contentDescription = review.namaUser,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.namaUser,
                    style = typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = colorScheme.secondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Format Tanggal (ISO String -> Readable)
            Text(
                text = formatDate(review.tanggal),
                style = typography.bodySmall.copy(
                    color = colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = review.komentar ?: "",
            style = typography.bodySmall.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(start = 4.dp, end = 4.dp)
        )
    }
}

// Helper format tanggal sederhana
fun formatDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(isoDate)
        outputFormat.format(date ?: "")
    } catch (e: Exception) {
        isoDate.take(10) // Fallback ambil 10 karakter pertama saja
    }
}