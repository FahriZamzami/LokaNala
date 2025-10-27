package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.R
import com.example.lokanala.model.Umkm
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.StarYellow
import com.example.lokanala.ui.theme.TextGrey

// PERBAIKAN: Mengembalikan InfoCard agar menerima objek Umkm dan NavController
@Composable
fun InfoCard(
    umkm: Umkm,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navController.navigate(umkm.navigationRoute) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(umkm.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(umkm.tag, fontSize = 14.sp, color = TextGrey)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = StarYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(umkm.rating.toString(), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Black)
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
            rating = 3.8,
            tag = "Katalog",
            imageRes = R.drawable.img_embun_coffee,
            navigationRoute = "merchant"
        )
        InfoCard(umkm = dummyUmkm, navController = rememberNavController())
    }
}
