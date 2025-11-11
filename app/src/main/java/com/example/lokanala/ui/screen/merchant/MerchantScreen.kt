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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
    navController: NavController,
    umkmId: Long
) {
    val colorScheme = MaterialTheme.colorScheme

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        item {
            MerchantHeader(
                onBack = { navController.popBackStack() },
                navController = navController,
                umkmId = umkmId,
                colorScheme = colorScheme
            )
        }
        item {
            PromoSection(
                modifier = Modifier.padding(horizontal = 16.dp),
                navController = navController,
                colorScheme = colorScheme
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colorScheme.surfaceVariant, thickness = 8.dp)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item { SearchAndFilterSection(modifier = Modifier.padding(horizontal = 16.dp), colorScheme = colorScheme) }
        item { Spacer(modifier = Modifier.height(16.dp)) }

        items(dummyProducts, key = { it.id }) { product ->
            MenuItemCard(
                product = product,
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { navController.navigate(Screen.Detail.createRoute(product.id)) }
            )
            HorizontalDivider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun MerchantHeader(
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
                .padding(top = 30.dp, start = 8.dp)
                .clip(CircleShape)
                .background(colorScheme.background.copy(alpha = 0.5f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = colorScheme.primary)
        }

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
                        Text(
                            "Seblak Sendik",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "4,5",
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
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Jl. Timor Manis No. 23, Padang...",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoSection(
    modifier: Modifier = Modifier,
    navController: NavController,
    colorScheme: ColorScheme
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
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PromoGreenText.copy(alpha = 0.5f)),
                onClick = { /* UI Saja */ }
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PAKET SEBLAK KOMPLIT + ES TEH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorScheme.onBackground,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Rp 19.000",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = colorScheme.onBackground
                    )
                    Text(
                        text = "Rp 22.000",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable { navController.navigate(Screen.Promo.route) },
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Lihat lainnya",
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Lihat semua promo",
                                tint = colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
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
        onClick = { /* UI Saja */ }
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