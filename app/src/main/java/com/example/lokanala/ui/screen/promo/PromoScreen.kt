package com.example.lokanala.ui.screen.promo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.ui.components.CustomerPromoCard
import com.example.lokanala.ui.components.CustomerPromoDetailPopup
import com.example.lokanala.ui.screen.promotion_umkm.Promotion
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoScreen(
    navController: NavController,
    umkmId: Long,
    viewModel: PromoViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(umkmId) {
        if (umkmId > 0) {
            viewModel.loadPromotionsForUmkm(umkmId.toInt())
        }
    }

    var sortedPromotions by remember { mutableStateOf<List<Promotion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.promotions.toList() }
            .collect { promotions ->
                val dateFormat = java.text.SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)
                sortedPromotions = promotions.sortedByDescending { promo ->
                    try {
                        dateFormat.parse(promo.startDate)?.time ?: 0L
                    } catch (_: Exception) {
                        0L
                    }
                }
            }
    }

    // Observe loading state
    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.loading }
            .collect { loading ->
                isLoading = loading
            }
    }

    var selectedPromotion by remember { mutableStateOf<Promotion?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Promo Menarik",
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
                }
            )
        },
    ) { paddingValues ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            sortedPromotions.isEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
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
                            )
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(sortedPromotions, key = { it.id }) { promotion ->
                        CustomerPromoCard(
                            promotion = promotion,
                            onClick = { selectedPromotion = it }
                        )
                    }
                }
            }
        }
    }

    selectedPromotion?.let { promo ->
        CustomerPromoDetailPopup(
            promotion = promo,
            onDismiss = { selectedPromotion = null }
        )
    }
}