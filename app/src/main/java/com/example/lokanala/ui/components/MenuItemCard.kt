package com.example.lokanala.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response.merchant.MerchantProduct
import com.example.lokanala.ui.theme.StarYellow
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MenuItemCard(
    product: MerchantProduct, // <-- Ubah tipe data ke model API
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔹 Gambar Produk (Menggunakan Coil)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.gambarUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.logo_lokanala), // Siapkan placeholder
            error = painterResource(R.drawable.logo_lokanala),     // Siapkan error image
            contentDescription = product.namaProduk,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 🔹 Informasi Produk
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = product.namaProduk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = colorScheme.onSurface
            )

            Text(
                text = product.deskripsi ?: "Tidak ada deskripsi",
                fontSize = 13.sp,
                color = colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Text(
                text = formatRupiah(product.harga),
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
                // Tampilkan jumlah ulasan dari API
                Text(
                    text = "4.5 (${product.jumlahUlasan} ulasan)",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Detail",
            tint = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

// Helper formatting lokal
private fun formatRupiah(number: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    return format.format(number).replace(",00", "")
}