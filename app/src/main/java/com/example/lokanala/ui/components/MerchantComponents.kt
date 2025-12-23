package com.example.lokanala.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response_and_request.merchant.MerchantData
import com.example.lokanala.data.remote.response_and_request.merchant.MerchantPromo
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.screen.my_merchant.SortOrder

@Composable
fun MerchantPublicHeader(
    data: MerchantData,
    onBack: () -> Unit,
    navController: NavController,
    umkmId: Long,
    isFollowing: Boolean,            
    onFollowClick: () -> Unit,         
    isOwner: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(data.imageHeaderUrl).crossfade(true).build(),
            placeholder = painterResource(R.drawable.logo_lokanala),
            error = painterResource(R.drawable.logo_lokanala),
            fallback = painterResource(R.drawable.logo_lokanala),
            contentDescription = "Header UMKM",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
        
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 30.dp, start = 8.dp).clip(CircleShape)
                .background(colorScheme.background.copy(alpha = 0.5f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = colorScheme.primary)
        }
        Card(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = data.imageHeaderUrl,
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        placeholder = painterResource(R.drawable.logo_lokanala),
                        error = painterResource(R.drawable.logo_lokanala),
                        fallback = painterResource(R.drawable.logo_lokanala)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(text = data.nama, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorScheme.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = data.rating.toString(), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colorScheme.secondary)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Filled.Star, "Rating", tint = colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                Text(text = data.kategori, fontSize = 10.sp, color = colorScheme.primary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(text = data.alamat ?: "Alamat tidak tersedia", fontSize = 13.sp, color = colorScheme.onSurfaceVariant, maxLines = 1)
                    }

                    
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!isOwner) {
                        Button(
                            onClick = onFollowClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) colorScheme.surfaceVariant else colorScheme.primary,
                                contentColor = if (isFollowing) colorScheme.onSurfaceVariant else colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isFollowing) "Following" else "Follow", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            
                            navController.navigate(Screen.UmkmDetail.createRoute(umkmId))
                        },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lihat Detail UMKM", fontWeight = FontWeight.Bold, color = colorScheme.primary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = colorScheme.primary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun MerchantPromoSection(
    promo: MerchantPromo,
    modifier: Modifier = Modifier,
    navController: NavController,
    umkmId: Long
) {
    val GreenPromoColor = Color(0xFF104618)
    Column(modifier = modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(text = "PROMO MENARIK", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GreenPromoColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = { navController.navigate(Screen.Promo.createRoute(umkmId)) }
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = promo.namaPromo.uppercase(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = promo.deskripsi ?: "Dapatkan penawaran menarik hari ini!", fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f), maxLines = 2)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Lihat lainnya", fontWeight = FontWeight.Bold, color = Color(0xFFFF6F6F), fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = Color(0xFFFF6F6F), modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantSearchFilter(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    sortOrder: SortOrder,
    onTogglePriceSort: () -> Unit,
    onTogglePopularSort: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari Produk...") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = colorScheme.primary) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = colorScheme.surfaceVariant,
                focusedContainerColor = colorScheme.surfaceVariant
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            item {
                MerchantFilterChip(text = "Semua", isSelected = selectedCategory == null && sortOrder != SortOrder.RATING_DESC, onClick = { onCategorySelected(null) })
            }
            item {
                MerchantCategoryDropdown(categories = categories, selectedCategory = selectedCategory, onCategorySelected = onCategorySelected)
            }
            item {
                MerchantFilterChip(text = "Terpopuler", isSelected = sortOrder == SortOrder.RATING_DESC, onClick = onTogglePopularSort)
            }
            item {
                MerchantFilterChip(
                    text = "Harga",
                    isSelected = sortOrder == SortOrder.PRICE_ASC || sortOrder == SortOrder.PRICE_DESC,
                    onClick = onTogglePriceSort,
                    trailingIcon = {
                        when (sortOrder) {
                            SortOrder.PRICE_ASC -> Icon(Icons.Default.ArrowUpward, null, modifier = Modifier.size(18.dp))
                            SortOrder.PRICE_DESC -> Icon(Icons.Default.ArrowDownward, null, modifier = Modifier.size(18.dp))
                            else -> Icon(Icons.Default.UnfoldMore, null, modifier = Modifier.size(18.dp))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MerchantFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text, fontWeight = FontWeight.SemiBold) },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colorScheme.surfaceVariant,
            selectedContainerColor = colorScheme.primaryContainer,
            labelColor = colorScheme.onSurface,
            selectedLabelColor = colorScheme.primary
        ),
        border = BorderStroke(1.dp, if (isSelected) colorScheme.primaryContainer else colorScheme.outlineVariant),
        trailingIcon = trailingIcon
    )
}

@Composable
private fun MerchantCategoryDropdown(
    modifier: Modifier = Modifier,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    Box(modifier = modifier) {
        FilterChip(
            selected = selectedCategory != null,
            onClick = { expanded = if (selectedCategory != null) true else !expanded },
            label = { Text(text = selectedCategory ?: "Kategori Produk", fontWeight = FontWeight.SemiBold, maxLines = 1) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp)) },
            shape = RoundedCornerShape(8.dp),
            colors = FilterChipDefaults.filterChipColors(
                containerColor = colorScheme.surfaceVariant,
                selectedContainerColor = if (selectedCategory != null) colorScheme.primaryContainer else colorScheme.surfaceVariant,
                labelColor = colorScheme.onSurface,
                selectedLabelColor = if (selectedCategory != null) colorScheme.primary else colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, if (selectedCategory != null) colorScheme.primaryContainer else colorScheme.outlineVariant)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (categories.isNotEmpty()) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal, maxLines = 1) },
                        onClick = {
                            if (selectedCategory == category) onCategorySelected(null) else onCategorySelected(category)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(textColor = if (selectedCategory == category) colorScheme.primary else colorScheme.onSurface)
                    )
                }
            } else {
                DropdownMenuItem(text = { Text("Belum ada kategori", style = MaterialTheme.typography.bodySmall) }, onClick = { expanded = false }, enabled = false)
            }
        }
    }
}

