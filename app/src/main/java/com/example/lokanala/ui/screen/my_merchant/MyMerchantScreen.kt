package com.example.lokanala.ui.screen.my_merchant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.R
import com.example.lokanala.ui.components.MyMenuItemCard // Menggunakan komponen baru
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.*

@Composable
fun MyMerchantScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    umkmId: Int, // ID UMKM dari MyUmkmScreen
    viewModel: MyMerchantViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddProduct.createRoute(umkmId))
                },
                containerColor = PromoPinkText,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "Tambah Produk")
            }
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            item {
                MerchantHeader(
                    onBack = { navController.popBackStack() }
                )
            }
            item {
                SearchAndFilterSection(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(products, key = { it.id }) { product ->
                MyMenuItemCard( // Menggunakan MyMenuItemCard
                    product = product,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onEditClick = {
                        // TODO: Navigasi ke halaman EditProduct, buat rute baru di Screen.kt
                        // Contoh: navController.navigate(Screen.EditProduct.createRoute(it.id))
                        // Untuk demo, hapus dulu produknya
                        viewModel.deleteProduct(it.id)
                    },
                    onDeleteClick = {
                        viewModel.deleteProduct(it.id)
                    }
                )
                HorizontalDivider(
                    color = Color(0xFFF5F5F5),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

// Reused component from MerchantScreen.kt
@Composable
private fun MerchantHeader(onBack: () -> Unit) { /* ... isi kode MerchantHeader dari MerchantScreen.kt ... */
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_seblak_header),
            contentDescription = "Header Seblak Sendik",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape)
                .background(Color(0x80000000))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_seblak_sendik),
                    contentDescription = "Logo Seblak Sendik",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Seblak Sendik", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("4,5", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextGrey)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Filled.Star, "Rating", tint = StarYellow, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Jl. Timor Manis No. 23, Padang...", fontSize = 13.sp, color = TextGreyLight, maxLines = 1)
                }
            }
        }

        Text(
            text = "Lihat detail UMKM >",
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = (-72).dp, x = (-16).dp)
                .clickable { /* UI Saja */ }
        )
    }
}


// Reused component from MerchantScreen.kt
@Composable
private fun SearchAndFilterSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = SearchPinkBg
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
                    tint = PromoPinkText
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Cari Produk",
                    fontSize = 15.sp,
                    color = PromoPinkText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChip("Tipe produk") }
            item { FilterChip("Terlaris") }
            item { FilterChip(text = "Harga", showArrow = true) }
        }
    }
}

// Reused component from MerchantScreen.kt
@Composable
private fun FilterChip(text: String, showArrow: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = FilterChipBg,
        border = BorderStroke(1.dp, FilterChipBorder),
        onClick = { /* UI Saja */ }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = TextGrey,
                fontWeight = FontWeight.SemiBold
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Filter Harga",
                    tint = TextGrey,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}