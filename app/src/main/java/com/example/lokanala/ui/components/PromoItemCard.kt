package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.R
import com.example.lokanala.model.Promo
import com.example.lokanala.ui.theme.LokanalaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoItemCard(
    promo: Promo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick
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
                    color = colorScheme.onSurface,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = promo.dateRange,
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant
                )

                promo.newPrice?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = it,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = colorScheme.primary
                        )
                        promo.oldPrice?.let { old ->
                            Text(
                                text = old,
                                fontSize = 14.sp,
                                color = colorScheme.onSurfaceVariant,
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
                dateRange = "8 Oktober 2025 - 15 Oktober 2025",
                newPrice = "Rp 18.000",
                oldPrice = "Rp 22.000",
                imageResDetail = R.drawable.img_promo_seblak_detail,
                termsAndConditions = emptyList(),
                howToUse = emptyList()
            ),
            modifier = Modifier.padding(16.dp),
            onClick = {}
        )
    }
}