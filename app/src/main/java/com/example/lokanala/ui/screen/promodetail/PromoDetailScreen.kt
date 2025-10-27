package com.example.lokanala.ui.screen.promodetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokanala.ui.components.InfoSection
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.PromoPinkBg
import com.example.lokanala.ui.theme.StrikeThroughGrey
import com.example.lokanala.ui.theme.TextGreyLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: PromoDetailViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val promo = uiState.promo

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Promo menarik",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PromoPinkBg, // Latar pink
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White // Latar belakang utama putih
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Gambar
            item {
                Image(
                    painter = painterResource(id = promo.imageResDetail),
                    contentDescription = promo.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Sesuaikan tinggi
                )
            }

            // 2. Judul dan Harga
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = promo.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        promo.newPrice?.let {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                        promo.oldPrice?.let {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                color = StrikeThroughGrey,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }

            // 3. Masa Berlaku
            item {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Masa Berlaku :",
                        fontSize = 14.sp,
                        color = TextGreyLight,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = promo.dateRange,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 4. Syarat dan Ketentuan
            item {
                ThickDivider()
                InfoSection(
                    title = "Syarat dan ketentuan",
                    items = promo.termsAndConditions,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // 5. Cara Gunakan Promo
            item {
                ThickDivider()
                InfoSection(
                    title = "Cara gunakan promo",
                    items = promo.howToUse,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // 6. Footer Pink
            item {
                ThickDivider()
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight() // Mengisi sisa ruang
                        .fillMaxWidth()
                        .background(PromoPinkBg)
                        .height(200.dp) // Beri tinggi minimal
                )
            }
        }
    }
}

// Divider tebal antar seksi
@Composable
private fun ThickDivider() {
    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 8.dp)
}

@Preview(showBackground = true)
@Composable
fun PromoDetailScreenPreview() {
    LokanalaTheme {
        val previewViewModel = PromoDetailViewModel(
            SavedStateHandle(mapOf("promoId" to 1))
        )
        PromoDetailScreen(viewModel = previewViewModel, onBack = {})
    }
}
