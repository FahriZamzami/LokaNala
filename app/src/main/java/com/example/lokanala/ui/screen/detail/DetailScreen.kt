package com.example.lokanala.ui.screen.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response.product.TopReviewData
import com.example.lokanala.ui.components.RatingItem
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.*
import com.example.lokanala.util.ImageUrlHelper

// Definisi Warna Khusus agar sesuai desain (Pink/Merah)
val PrimaryPink = Color(0xFFD81B60) // Sesuaikan dengan warna di screenshot
val TextGrey = Color(0xFF757575)
val StarYellow = Color(0xFFFFC107)

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(),
    navController: NavController,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val product = uiState.product

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "Error",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.error
            )
        } else {
            // DATA ADA
            if (product != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. GAMBAR HEADER PRODUK
                    item {
                        Box {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(ImageUrlHelper.getFullImageUrl(product.gambarUrl))
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.logo_lokanala),
                                error = painterResource(R.drawable.logo_lokanala),
                                fallback = painterResource(R.drawable.logo_lokanala),
                                contentDescription = product.namaProduk,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp) // Tinggi gambar sesuai desain
                            )
                        }
                    }

                    // 2. KONTEN DETAIL (Overlap ke atas)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-20).dp) // Efek overlap lengkungan
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp)
                        ) {
                            // Bagian Judul dan Harga
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.namaProduk,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    RatingChip(product.rating, product.jumlahUlasan)
                                }
                                Text(
                                    text = viewModel.formatRupiah(product.harga),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = PrimaryPink // Warna Harga Pink
                                )
                            }

                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), thickness = 1.dp)
                            Spacer(Modifier.height(16.dp))

                            // Deskripsi Produk
                            ProductDescription(description = product.deskripsi ?: "Tidak ada deskripsi")

                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), thickness = 6.dp) // Divider tebal
                            Spacer(Modifier.height(16.dp))

                            // Bagian Rating
                            RatingSection(
                                productId = product.id,
                                topReview = product.ulasanTerbaik,
                                navController = navController
                            )
                        }
                    }
                }

                // TOP BAR FLOATING
                DetailTopBar(
                    onBack = onBack,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun DetailTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconModifier = Modifier
            .clip(CircleShape)
            .background(Color(0x66000000)) // Transparan Hitam

        IconButton(onClick = onBack, modifier = iconModifier) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
        }
        Row {
            IconButton(onClick = { /* TODO */ }, modifier = iconModifier) {
                Icon(Icons.Default.Search, "Cari", tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { /* TODO */ }, modifier = iconModifier) {
                Icon(Icons.Default.Share, "Bagikan", tint = Color.White)
            }
        }
    }
}

@Composable
private fun RatingChip(rating: Double, reviewCount: Int) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Star,
                "Rating",
                tint = StarYellow, // Warna Bintang Kuning
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "$rating ($reviewCount)",
                fontSize = 13.sp,
                color = TextGrey,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProductDescription(description: String) {
    Column {
        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(12.dp))

        // Tombol "selengkapnya" di tengah bawah
        Text(
            text = "selengkapnya",
            fontSize = 13.sp,
            color = TextGrey,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Expand logic */ },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RatingSection(
    productId: Int,
    topReview: TopReviewData?,
    navController: NavController
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Rating", // Sesuai screenshot
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Lihat semua >",
                fontSize = 13.sp,
                color = PrimaryPink, // Warna Pink
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Rating.createRoute(productId))
                }
            )
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(Modifier.height(16.dp))

        if (topReview != null) {
            RatingItem(review = topReview)
        } else {
            Text(
                text = "Belum ada ulasan.",
                fontSize = 13.sp,
                color = TextGrey,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}