package com.example.lokanala.ui.screen.promo_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.lokanala.ui.theme.PromoPinkText
import com.example.lokanala.ui.theme.TextGrey

@Composable
fun PromoDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: PromoDetailViewModel = viewModel(),
    onBack: () -> Unit
) {
    val promo by viewModel.promo.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(promo.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                    Spacer(Modifier.height(8.dp))
                    Text("Berlaku hingga ${promo.dateRange}", fontSize = 14.sp, color = TextGrey)

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 8.dp)
                    Spacer(Modifier.height(16.dp))

                    InfoSection(title = "Syarat & Ketentuan", items = promo.termsAndConditions)

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 8.dp)
                    Spacer(Modifier.height(16.dp))

                    InfoSection(title = "Cara Penggunaan", items = promo.howToUse)
                }
            }
        }

        DetailTopBar(onBack = onBack, modifier = Modifier.align(Alignment.TopCenter))

        Button(
            onClick = { /* UI Saja */ },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PromoPinkText)
        ) {
            Text("Pakai Kupon")
        }
    }
}

@Composable
private fun DetailTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconModifier = Modifier.clip(CircleShape).background(Color(0x80000000))
        IconButton(onClick = onBack, modifier = iconModifier) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
        }
        IconButton(onClick = { /* UI Saja */ }, modifier = iconModifier) {
            Icon(Icons.Default.Share, "Bagikan", tint = Color.White)
        }
    }
}

@Composable
private fun InfoSection(title: String, items: List<String>) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEachIndexed { index, item ->
                Row {
                    Text("${index + 1}. ", fontSize = 14.sp, color = TextGrey)
                    Text(item, fontSize = 14.sp, color = TextGrey, lineHeight = 20.sp)
                }
            }
        }
    }
}
