package com.example.lokanala.ui.screen.promotion_umkm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.components.MyUMKMPromotionCard
import com.example.lokanala.ui.components.UMKMPromotionDetailPopup
import com.example.lokanala.ui.theme.LokanalaTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUMKMPromotionScreen(
    navController: NavController,
    viewModel: PromotionViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme

    val promotions by remember { derivedStateOf { viewModel.promotions } }

    val sortedPromotions by remember(promotions) {
        derivedStateOf {
            val dateFormat = java.text.SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)
            promotions.sortedByDescending { promo ->
                try {
                    dateFormat.parse(promo.startDate)?.time ?: 0L
                } catch (_: Exception) {
                    0L
                }
            }
        }
    }

    var selectedPromotion by remember { mutableStateOf<Promotion?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Promosi UMKM Saya",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onSurface,
                    navigationIconContentColor = colorScheme.onSurfaceVariant
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("add_promotion") },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Tambah Promosi"
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Tambah Promosi",
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onPrimary
                )
            }
        },
        containerColor = colorScheme.background
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            if (sortedPromotions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada promosi UMKM.",
                            color = colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(
                    items = sortedPromotions,
                    key = { it.id }
                ) { promotion ->
                    MyUMKMPromotionCard(
                        promotion = promotion,
                        onEdit = { navController.navigate("edit_promotion/${promotion.id}") },
                        onDelete = { viewModel.deletePromotion(promotion.id) },
                        onItemClick = { selectedPromotion = it }
                    )
                }
            }
        }
    }

    selectedPromotion?.let { promo ->
        UMKMPromotionDetailPopup(
            promotion = promo,
            onDismiss = { selectedPromotion = null },
            onEdit = {
                selectedPromotion = null
                navController.navigate("edit_promotion/${promo.id}")
            },
            onDelete = {
                viewModel.deletePromotion(promo.id)
                selectedPromotion = null
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyUMKMPromotionScreen() {
    LokanalaTheme {
        MyUMKMPromotionScreen(navController = rememberNavController())
    }
}