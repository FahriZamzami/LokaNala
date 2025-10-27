package com.example.lokanala.ui.screen.promo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokanala.ui.components.PromoItemCard
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.PromoPinkBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoScreen(
    modifier: Modifier = Modifier,
    viewModel: PromoViewModel = viewModel(),
    onBack: () -> Unit,
    onPromoClick: (Int) -> Unit // <-- Parameter baru untuk navigasi
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    containerColor = PromoPinkBg,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = PromoPinkBg,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(uiState.promos, key = { it.id }) { promo ->
                PromoItemCard(
                    promo = promo,
                    onClick = { onPromoClick(promo.id) } // <-- Meneruskan ID promo
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PromoScreenPreview() {
    LokanalaTheme {
        PromoScreen(onBack = {}, onPromoClick = {}) // <-- Menambahkan parameter di preview
    }
}
