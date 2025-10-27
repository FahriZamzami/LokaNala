package com.example.lokanala.ui.screen.merchant

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lokanala.R
import com.example.lokanala.model.dummyProducts
import com.example.lokanala.ui.components.MenuItemCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.*

@Composable
fun MerchantScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            MerchantHeader(
                onBack = { navController.popBackStack() }
            )
        }
        item {
            PromoSection(
                modifier = Modifier.padding(horizontal = 16.dp),
                navController = navController
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // PERBAIKAN: Mengganti Divider menjadi HorizontalDivider
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 8.dp)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item { SearchAndFilterSection(modifier = Modifier.padding(horizontal = 16.dp)) }
        item { Spacer(modifier = Modifier.height(16.dp)) }

        items(dummyProducts, key = { it.id }) { product ->
            MenuItemCard(
                product = product,
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    navController.navigate(Screen.Detail.createRoute(product.id))
                }
            )
            // PERBAIKAN: Mengganti Divider menjadi HorizontalDivider
            HorizontalDivider(
                color = Color(0xFFF5F5F5),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun MerchantHeader(
    onBack: () -> Unit
) {
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

@Composable
private fun PromoSection(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "PROMO MENARIK",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PromoGreenBg),
                onClick = { /* UI Saja */ }
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        text = "PAKET SEBLAK KOMPLIT + ES TEH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PromoGreenText,
                        maxLines = 2,
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rp 19.000",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Rp 22.000",
                        fontSize = 12.sp,
                        color = StrikeThroughGrey,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Card(
                modifier = Modifier.weight(0.7f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PromoPinkBg),
                onClick = {
                    navController.navigate(Screen.Promo.route)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Lihat\nlainnya",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PromoPinkText,
                        lineHeight = 18.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Lihat Semua Promo",
                        tint = PromoPinkText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun MerchantScreenPreview() {
    LokanalaTheme {
        // Preview tidak bisa menampilkan screen yang butuh NavController
    }
}
