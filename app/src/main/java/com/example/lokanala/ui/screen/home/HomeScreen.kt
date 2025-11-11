package com.example.lokanala.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
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
import com.example.lokanala.ui.theme.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeTopBar(navController = navController)

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeSearchBar()
                FilterChips()
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 100.dp
                )
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            LokanalaBottomBar(
                navController = navController,
                currentRoute = Screen.Home.route
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = "Lokasi",
                tint = colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Limau Manis, Unand",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                navController.navigate(Screen.Notification.route)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifikasi",
                    tint = colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FilterChips(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { FilterChipItem("Tipe UMKM", isSelected = false) }
        item { FilterChipItem("Terlaris", isSelected = false) }
        item { FilterChipItem("Trending", isSelected = true) }
    }
}

@Composable
private fun FilterChipItem(text: String, isSelected: Boolean) {
    val colorScheme = MaterialTheme.colorScheme

    val (bgColor, textColor, border) = if (isSelected) {
        Triple(
            colorScheme.primary.copy(alpha = 0.15f),
            colorScheme.primary,
            BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f))
        )
    } else {
        Triple(
            colorScheme.surfaceVariant.copy(alpha = 0.5f),
            colorScheme.onSurfaceVariant,
            BorderStroke(0.dp, Color.Transparent)
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = border,
        onClick = { /* TODO: Filter action */ }
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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