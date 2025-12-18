package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.R
import com.example.lokanala.model.Umkm
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.StarYellow

@Composable
fun InfoCard(
    umkm: Umkm,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navController.navigate(umkm.navigationRoute) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column {
            Image(
                painter = painterResource(id = umkm.imageRes),
                contentDescription = umkm.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = umkm.name,
                        style = typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = umkm.tag,
                        style = typography.bodyMedium.copy(color = colorScheme.onSurfaceVariant)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = StarYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", umkm.rating),
                        style = typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoCardPreview() {
    LokanalaTheme {
        val dummyUmkm = Umkm(
            id = 1,
            name = "Embun Coffee Space",
            rating = 4.8,
            tag = "Coffee Shop",
            imageRes = R.drawable.img_embun_coffee,
            navigationRoute = "merchant"
        )
        InfoCard(umkm = dummyUmkm, navController = rememberNavController())
    }
}