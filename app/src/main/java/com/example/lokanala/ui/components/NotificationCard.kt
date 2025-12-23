package com.example.lokanala.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.lokanala.model.NotificationItem
import com.example.lokanala.ui.theme.LokanalaTheme

@Composable
fun NotificationCard(
    notification: NotificationItem,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.description,
                fontSize = 13.sp,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
            if (notification.productName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notification.productName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationCardPreview() {
    LokanalaTheme {
        NotificationCard(
            notification = NotificationItem(
                id = 1,
                title = "ULASAN BARU",
                description = "Ada ulasan baru untuk produk",
                productName = "Seblak Spesial Komplit"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}