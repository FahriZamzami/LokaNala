package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.R
import com.example.lokanala.model.Product
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.StarYellow
import com.example.lokanala.ui.theme.TextGrey
import com.example.lokanala.ui.theme.TextGreyLight

@Composable
fun MenuItemCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = product.description,
                fontSize = 13.sp,
                color = TextGrey,
                maxLines = 2
            )
            Text(
                text = product.price,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = StarYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${product.rating} (${product.reviewCount})",
                    fontSize = 13.sp,
                    color = TextGreyLight
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Detail",
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MenuItemCardPreview() {
    LokanalaTheme {
        MenuItemCard(
            product = Product(
                id = 1,
                name = "Seblak Level 5",
                description = "Seblak Khas Bandung isi kerupuk, sosis, dan seafood.",
                price = "Rp 15.000",
                rating = 4.7,
                reviewCount = 60,
                imageRes = R.drawable.img_seblak_level_5,
                imageResDetail = R.drawable.img_seblak_detail
            ),
            modifier = Modifier.padding(16.dp),
            onClick = {}
        )
    }
}
