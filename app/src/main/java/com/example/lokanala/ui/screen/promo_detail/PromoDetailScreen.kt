package com.example.lokanala.ui.screen.promo_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokanala.ui.theme.LokanalaTheme

@Composable
fun PromoDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: PromoDetailViewModel = viewModel(),
    onBack: () -> Unit
) {
    val promo by viewModel.promo.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.surface),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = promo.imageResDetail),
                    contentDescription = promo.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        promo.title,
                        style = typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    )

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Berlaku hingga ${promo.dateRange}",
                        style = typography.bodyMedium.copy(color = colorScheme.onSurfaceVariant)
                    )

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(
                        color = colorScheme.surfaceVariant,
                        thickness = 8.dp
                    )
                    Spacer(Modifier.height(16.dp))

                    InfoSection(title = "Syarat & Ketentuan", items = promo.termsAndConditions)

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(
                        color = colorScheme.surfaceVariant,
                        thickness = 8.dp
                    )
                    Spacer(Modifier.height(16.dp))

                    InfoSection(title = "Cara Penggunaan", items = promo.howToUse)
                }
            }
        }

        DetailTopBar(
            onBack = onBack,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        Button(
            onClick = { /* UI saja */ },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                "Pakai Kupon",
                style = typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
private fun DetailTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconBackground = colorScheme.surface.copy(alpha = 0.8f)
        val iconTint = colorScheme.primary

        val iconModifier = Modifier
            .clip(CircleShape)
            .background(iconBackground)

        IconButton(onClick = onBack, modifier = iconModifier) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = iconTint
            )
        }
        IconButton(onClick = { /* UI Saja */ }, modifier = iconModifier) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Bagikan",
                tint = iconTint
            )
        }
    }
}

@Composable
private fun InfoSection(title: String, items: List<String>) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column {
        Text(
            text = title,
            style = typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
        )
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEachIndexed { index, item ->
                Row {
                    Text(
                        "${index + 1}. ",
                        style = typography.bodyMedium.copy(color = colorScheme.onSurfaceVariant)
                    )
                    Text(
                        item,
                        style = typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PromoDetailScreenPreview() {
    LokanalaTheme {
        PromoDetailScreen(onBack = {})
    }
}