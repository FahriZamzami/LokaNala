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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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

    // 🔹 Gunakan `collectAsState()` hanya jika promotions adalah Flow/LiveData
    val promotions by remember { derivedStateOf { viewModel.promotions } }

    // 🔹 Hindari sorting di setiap recomposition
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
                title = { Text("My UMKM Promotion", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary.copy(alpha = 0.15f))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("add_promotion") },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Promotion", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        // 🔹 Hindari heavy drawing di setiap frame: gunakan remember
        val backgroundModifier = remember {
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = colorScheme.primary.copy(alpha = 0.08f),
                        radius = 450f,
                        center = Offset(size.width * 0.9f, size.height * 0.1f)
                    )
                    drawCircle(
                        color = colorScheme.primary.copy(alpha = 0.05f),
                        radius = 350f,
                        center = Offset(size.width * 0.1f, size.height * 0.9f)
                    )
                }
        }

        Box(modifier = backgroundModifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sortedPromotions, key = { it.id }) { promotion ->
                    MyUMKMPromotionCard(
                        promotion = promotion,
                        onEdit = { navController.navigate("edit_promotion/${promotion.id}") },
                        onDelete = { viewModel.deletePromotion(promotion.id) },
                        onItemClick = { selectedPromotion = it }
                    )
                }

                // Spacer agar FAB tidak menutupi item terakhir
                item { Spacer(Modifier.height(100.dp)) }
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
