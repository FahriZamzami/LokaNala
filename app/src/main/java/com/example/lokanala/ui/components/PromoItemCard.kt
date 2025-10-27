package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.R
import com.example.lokanala.model.Promo
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.NotSelected
import com.example.lokanala.ui.theme.TextGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoItemCard(
    promo: Promo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick // PERBAIKAN: Menambahkan onClick ke Card
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_seblak_sendik),
                contentDescription = promo.title,
                modifier = Modifier.size(56.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = promo.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = promo.dateRange,
                    fontSize = 13.sp,
                    color = TextGrey
                )
                promo.newPrice?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = it,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        promo.oldPrice?.let {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                color = NotSelected,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PromoItemCardPreview() {
    LokanalaTheme {
        PromoItemCard(
            promo = Promo(
                id = 1,
                title = "PAKET SEBLAK KOMPLIT + ES TEH",
                dateRange = "8 October 2025 - 15 October 2025",
                newPrice = "Rp 18.000",
                oldPrice = "Rp 22.000",
                imageResDetail = R.drawable.img_promo_seblak_detail, // PERBAIKAN: Menambahkan data untuk preview
                termsAndConditions = emptyList(),
                howToUse = emptyList()
            ),
            modifier = Modifier.padding(16.dp),
            onClick = {} // PERBAIKAN: Menambahkan aksi klik untuk preview
        )
    }
}
