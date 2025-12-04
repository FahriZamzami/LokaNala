package com.example.lokanala.ui.screen.merchant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response.merchant.MerchantData
import com.example.lokanala.data.remote.response.merchant.MerchantPromo
import com.example.lokanala.ui.components.MenuItemCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.*

@Composable
fun MerchantScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    umkmId: Long,
    viewModel: MerchantViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    // 1. Panggil API saat layar pertama kali dibuka
    LaunchedEffect(umkmId) {
        viewModel.loadMerchantDetail(umkmId)
    }

    Box(modifier = modifier.fillMaxSize().background(colorScheme.background)) {
        // --- HANDLING STATE ---
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null) {
            // Tampilkan pesan error jika gagal load
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.errorMessage ?: "Terjadi kesalahan",
                    color = colorScheme.error
                )
                Button(onClick = { viewModel.loadMerchantDetail(umkmId) }) {
                    Text("Coba Lagi")
                }
            }
        } else {
            // --- DATA SUKSES DIMUAT ---
            val merchant = uiState.merchantData

            if (merchant != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // A. HEADER UMKM (Foto & Info)
                    item {
                        MerchantHeader(
                            data = merchant,
                            onBack = { navController.popBackStack() },
                            navController = navController,
                            umkmId = umkmId,
                            colorScheme = colorScheme
                        )
                    }

                    // B. PROMO TERBARU (Hanya muncul jika ada promo)
                    // Ambil promo pertama dari list (karena di controller sudah di sort by ID desc)
                    val latestPromo = merchant.promos.firstOrNull()

                    if (latestPromo != null) {
                        item {
                            PromoSection(
                                promo = latestPromo,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                navController = navController
                            )
                        }
                    }

                    // C. DIVIDER, SEARCH & FILTER
                    item {
                        // Beri jarak agak jauh dari promo/header
                        Spacer(modifier = Modifier.height(24.dp))

                        SearchAndFilterSection(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            colorScheme = colorScheme
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    // D. LIST PRODUK
                    if (merchant.products.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada produk tersedia.",
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(merchant.products, key = { it.idProduk }) { product ->
                            MenuItemCard(
                                product = product,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onClick = {
                                    // Navigasi ke Detail Produk
                                    navController.navigate(Screen.Detail.createRoute(product.idProduk))
                                }
                            )
                            // Garis pemisah tipis antar produk
                            HorizontalDivider(
                                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    // Padding bawah extra agar item terakhir tidak tertutup navigasi
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// KOMPONEN UI
// -----------------------------------------------------------------

@Composable
private fun MerchantHeader(
    data: MerchantData,
    onBack: () -> Unit,
    navController: NavController,
    umkmId: Long,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        // 1. GAMBAR HEADER UTAMA
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.imageHeaderUrl)
                .crossfade(true)
                .build(),

            // Gambar Placeholder (Saat Loading) - bisa pakai gambar abu-abu atau logo
            placeholder = painterResource(R.drawable.logo_lokanala),

            // Gambar Error (Jika URL 404/Offline) -> Pakai Logo Lokanala
            error = painterResource(R.drawable.logo_lokanala),

            // Gambar Fallback (Jika URL dari database NULL) -> Pakai Logo Lokanala
            fallback = painterResource(R.drawable.logo_lokanala),

            contentDescription = "Header UMKM",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        // Tombol Back
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 30.dp, start = 8.dp)
                .clip(CircleShape)
                .background(colorScheme.background.copy(alpha = 0.5f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = colorScheme.primary)
        }

        // Kartu Informasi UMKM (Floating Card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Logo Kecil (Bulat) - Menggunakan logika fallback yang sama
                    AsyncImage(
                        model = data.imageHeaderUrl,
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        placeholder = painterResource(R.drawable.logo_lokanala),
                        error = painterResource(R.drawable.logo_lokanala),
                        fallback = painterResource(R.drawable.logo_lokanala)
                    )

                    Spacer(Modifier.width(16.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = data.nama,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = data.rating.toString(),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = colorScheme.secondary
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.Star,
                                "Rating",
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Kategori Label
                            Surface(
                                color = colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = data.kategori,
                                    fontSize = 10.sp,
                                    color = colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = data.alamat ?: "Alamat tidak tersedia",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Link Detail
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("detailscreen/$umkmId") },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Lihat Detail UMKM",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Lihat detail",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PromoSection(
    promo: MerchantPromo,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    // Warna Hijau Tua (Sesuai Screenshot)
    val GreenPromoColor = Color(0xFF104618)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "PROMO MENARIK",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = GreenPromoColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = {
                navController.navigate(Screen.Promo.route)
            }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Nama Promo
                Text(
                    text = promo.namaPromo.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Deskripsi Promo (Karena harga tidak ada)
                Text(
                    text = promo.deskripsi ?: "Dapatkan penawaran menarik hari ini!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Link "Lihat lainnya"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lihat lainnya",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F6F), // Warna Pink/Merah Muda
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFFFF6F6F),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Search Bar Tampilan Saja
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari Produk",
                    tint = colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Cari Produk",
                    fontSize = 15.sp,
                    color = colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips (Scrollable)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChip(text = "Tipe produk", colorScheme = colorScheme) }
            item { FilterChip(text = "Terlaris", colorScheme = colorScheme) }
            item { FilterChip(text = "Harga", showArrow = true, colorScheme = colorScheme) }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    showArrow: Boolean = false,
    colorScheme: ColorScheme
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, colorScheme.outlineVariant),
        onClick = { /* TODO: Implement Filter Logic */ }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Filter Harga",
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}