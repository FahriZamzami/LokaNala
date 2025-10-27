package com.example.lokanala.ui.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.model.Review
import com.example.lokanala.ui.components.RatingItem
import com.example.lokanala.ui.theme.*

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(),
    navController: NavController, // ✅ Tambahkan ini
    onBack: () -> Unit
) {
    val product by viewModel.product.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    product?.let { p ->
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                item {
                    Image(
                        painter = painterResource(id = p.imageResDetail),
                        contentDescription = p.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                                Spacer(Modifier.height(8.dp))
                                RatingChip(p.rating, p.reviewCount)
                            }
                            Text(p.price, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        }

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = FilterChipBg)
                        Spacer(Modifier.height(16.dp))

                        ProductDescription(description = p.description)

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 8.dp)
                        Spacer(Modifier.height(16.dp))

                        // ✅ Kirim navController ke RatingSection
                        RatingSection(reviews = reviews, navController = navController)
                    }
                }
            }

            DetailTopBar(
                onBack = onBack,
                modifier = Modifier.align(Alignment.TopCenter)
            )
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
        Row {
            IconButton(onClick = { /* UI Saja */ }, modifier = iconModifier) {
                Icon(Icons.Default.Search, "Cari", tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { /* UI Saja */ }, modifier = iconModifier) {
                Icon(Icons.Default.Share, "Bagikan", tint = Color.White)
            }
        }
    }
}

@Composable
private fun RatingChip(rating: Double, reviewCount: Int) {
    Surface(shape = RoundedCornerShape(8.dp), color = FilterChipBg) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Star, "Rating", tint = TextGrey, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("$rating ($reviewCount)", fontSize = 13.sp, color = TextGrey, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ProductDescription(description: String) {
    Column {
        Text(description, fontSize = 14.sp, color = TextGrey, lineHeight = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("Isian Seblak Khas Bandung:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextGrey)
        Spacer(Modifier.height(4.dp))
        Column(Modifier.padding(start = 8.dp)) {
            Text("• Kerupuk", fontSize = 14.sp, color = TextGrey)
            Text("• Sosis", fontSize = 14.sp, color = TextGrey)
            Text("• Seafood", fontSize = 14.sp, color = TextGrey)
        }
        Spacer(Modifier.height(16.dp))
        Text("selengkapnya", fontSize = 13.sp, color = TextGreyLight, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun RatingSection(
    reviews: List<Review>,
    navController: NavController // ✅ Tambahkan ini
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Rating", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(
                text = "Lihat semua >",
                fontSize = 13.sp,
                color = PromoPinkText,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    navController.navigate("rating") // ✅ Navigasi ke halaman RatingScreen
                }
            )
        }
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = FilterChipBg)
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            reviews.forEach { review ->
                RatingItem(review = review)
            }
        }
    }
}
