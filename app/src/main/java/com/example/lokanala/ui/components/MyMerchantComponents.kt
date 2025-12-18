package com.example.lokanala.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.draw.alpha
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
import com.example.lokanala.data.remote.response.MerchantData
import com.example.lokanala.model.Product
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.screen.my_merchant.SortOrder

val PromoGreenBg = Color(0xFFE8F5E9)

// ==========================================
// COMPONENT: MERCHANT HEADER
// ==========================================
@Composable
fun MerchantHeader(
    onBack: () -> Unit,
    navController: NavController,
    umkmId: Int,
    merchantData: MerchantData?
) {
    val colorScheme = MaterialTheme.colorScheme
    val nama = merchantData?.nama ?: "Memuat..."
    val alamat = merchantData?.alamat ?: "Alamat tidak tersedia"
    val rating = merchantData?.rating ?: 0.0
    val headerImage = merchantData?.linkLokasi
    val logoImage = merchantData?.linkLokasi
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        if (merchantData == null) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.White))
        } else {
            val headerUrl = headerImage?.takeIf { it.isNotBlank() }
            if (headerUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(headerUrl).crossfade(true).build(),
                    placeholder = painterResource(R.drawable.logo_lokanala),
                    error = painterResource(R.drawable.logo_lokanala),
                    fallback = painterResource(R.drawable.logo_lokanala),
                    contentDescription = "Header Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            } else {
                AsyncImage(
                    model = R.drawable.logo_lokanala,
                    contentDescription = "Header Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        }

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
                    if (merchantData == null) {
                        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.White))
                    } else {
                        val logoUrl = logoImage?.takeIf { it.isNotBlank() }
                        if (logoUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(logoUrl).crossfade(true).build(),
                                placeholder = painterResource(R.drawable.logo_lokanala),
                                error = painterResource(R.drawable.logo_lokanala),
                                fallback = painterResource(R.drawable.logo_lokanala),
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(60.dp).clip(CircleShape)
                            )
                        } else {
                            AsyncImage(
                                model = R.drawable.logo_lokanala,
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(60.dp).clip(CircleShape)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(text = nama, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorScheme.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = String.format("%.1f", rating), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colorScheme.secondary)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Filled.Star, "Rating", tint = colorScheme.secondary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(text = alamat, fontSize = 13.sp, color = colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate("detailscreen/$umkmId") },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lihat Tampilan Publik", fontWeight = FontWeight.Bold, color = colorScheme.primary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = colorScheme.primary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

// ==========================================
// COMPONENT: PROMO SECTION
// ==========================================
@Composable
fun MyMerchantPromoSection(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(text = "PROMO AKTIF", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorScheme.onBackground)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PromoGreenBg),
                onClick = { }
            ) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Paket Hemat Spesial", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colorScheme.onSurface, maxLines = 2, modifier = Modifier.fillMaxWidth())
                    Text(text = "Rp 15.000", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colorScheme.onSurface)
                }
            }
        }
    }
}

// ==========================================
// COMPONENT: SEARCH & FILTER
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMerchantSearchFilter(
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
                MyMerchantFilterChip(text = "Semua", isSelected = selectedCategory == null && sortOrder != SortOrder.RATING_DESC, onClick = { onCategorySelected(null) })
            }
            item {
                MyMerchantCategoryDropdown(categories = categories, selectedCategory = selectedCategory, onCategorySelected = onCategorySelected)
            }
            item {
                MyMerchantFilterChip(text = "Terpopuler", isSelected = sortOrder == SortOrder.RATING_DESC, onClick = onTogglePopularSort)
            }
            item {
                MyMerchantFilterChip(
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

@Composable
private fun MyMerchantCategoryDropdown(
    modifier: Modifier = Modifier,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    Box(modifier = modifier.alpha(1f)) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyMerchantFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text, fontWeight = FontWeight.SemiBold) },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outlineVariant),
        trailingIcon = trailingIcon
    )
}

// ==========================================
// COMPONENT: FAB MENU
// ==========================================
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MyMerchantFabMenu(
    umkmId: Int,
    navController: NavController,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Screen.ManageCategory.createRoute(umkmId)); onExpandedChange(false) },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    icon = { Icon(Icons.Filled.Category, "Kategori") },
                    text = { Text("Atur Kategori") }
                )
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Screen.AddProduct.createRoute(umkmId)); onExpandedChange(false) },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    icon = { Icon(Icons.Filled.AddShoppingCart, "Tambah Produk") },
                    text = { Text("Tambah Produk") }
                )
            }
        }
        FloatingActionButton(
            onClick = { onExpandedChange(!isExpanded) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            AnimatedContent(targetState = isExpanded, label = "") { expanded ->
                if (expanded) Icon(Icons.Default.Close, "Tutup") else Icon(Icons.Filled.Add, "Tambah")
            }
        }
    }
}

// ==========================================
// COMPONENT: DELETE PRODUCT DIALOG
// ==========================================
@Composable
fun DeleteProductDialog(
    product: Product,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Delete, null, tint = colorScheme.error, modifier = Modifier.size(36.dp)) },
        title = { Text(text = "Hapus Produk", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colorScheme.error) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorScheme.errorContainer.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Produk yang akan dihapus:", fontSize = 13.sp, color = colorScheme.onSurfaceVariant)
                        Text(text = product.name ?: "Nama tidak tersedia", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorScheme.error)
                        product.description?.takeIf { it.isNotBlank() }?.let {
                            Text(text = it, fontSize = 12.sp, color = colorScheme.onSurfaceVariant, maxLines = 2)
                        }
                        product.price?.let {
                            Text(text = it, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colorScheme.primary)
                        }
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Warning, null, tint = colorScheme.error, modifier = Modifier.size(24.dp))
                            Text(text = "Peringatan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorScheme.error)
                        }
                        Divider(color = colorScheme.error.copy(alpha = 0.3f), thickness = 1.dp)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Info, null, tint = colorScheme.onErrorContainer, modifier = Modifier.size(18.dp))
                            Text(text = "Tindakan ini tidak dapat dibatalkan. Produk akan dihapus secara permanen dari sistem.", fontSize = 13.sp, color = colorScheme.onErrorContainer, lineHeight = 18.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error), shape = RoundedCornerShape(8.dp)) {
                Text(text = "Ya, Hapus", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = colorScheme.surface
    )
}

