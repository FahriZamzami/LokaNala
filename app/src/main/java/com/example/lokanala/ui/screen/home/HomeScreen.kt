package com.example.lokanala.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.components.HomeSearchBar
import com.example.lokanala.ui.components.LokanalaBottomBar
import com.example.lokanala.ui.components.UmkmCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.ChipGrayBg
import com.example.lokanala.ui.theme.ChipPinkBgSelected
import com.example.lokanala.ui.theme.ChipPinkBorder
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.PromoPinkText

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { HomeTopBar(navController = navController) }, // PERBAIKAN
        bottomBar = {
            LokanalaBottomBar(
                navController = navController,
                currentRoute = Screen.Home.route
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeSearchBar(modifier = Modifier.padding(top = 16.dp))
            FilterChips()

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.umkmList, key = { it.id }) { umkm ->
                    UmkmCard(
                        umkm = umkm,
                        onClick = {
                            navController.navigate(Screen.Merchant.createRoute(umkm.id.toLong()))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    modifier: Modifier = Modifier,
    navController: NavController // PERBAIKAN
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Place,
            contentDescription = "Lokasi",
            tint = PromoPinkText
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Limau Manis, Unand",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            navController.navigate(Screen.Notification.route)
        }) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifikasi",
                tint = Color.Black
            )
        }
    }
}

// ... (Sisa kode tidak berubah)

@Composable
private fun FilterChips(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip("Tipe UMKM", isSelected = false)
        }
        item {
            FilterChip("Terlaris", isSelected = false)
        }
        item {
            FilterChip("Trending", isSelected = true)
        }
    }
}

@Composable
private fun FilterChip(text: String, isSelected: Boolean) {
    val (bgColor, textColor, border) = if (isSelected) {
        Triple(ChipPinkBgSelected, PromoPinkText, BorderStroke(1.dp, ChipPinkBorder))
    } else {
        Triple(ChipGrayBg, Color.Black, BorderStroke(0.dp, Color.Transparent))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = border,
        onClick = { /* TODO */ }
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LokanalaTheme {
        HomeScreen(navController = rememberNavController())
    }
}
