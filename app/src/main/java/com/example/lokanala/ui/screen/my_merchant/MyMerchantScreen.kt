package com.example.lokanala.ui.screen.my_merchant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.R
import com.example.lokanala.model.Product
import com.example.lokanala.ui.components.MyMenuItemCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.screen.category.Category
import com.example.lokanala.ui.screen.category.CategoryViewModel
import com.example.lokanala.ui.theme.PromoGreenBg

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun MyMerchantScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    umkmId: Int,
    viewModel: MyMerchantViewModel = viewModel(),
    categoryViewModel: CategoryViewModel
) {
    val productGroups by viewModel.productGroups.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val categories = categoryViewModel.categories

    val colorScheme = MaterialTheme.colorScheme
    var isFabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                navController.navigate("promotion/1")
                                isFabExpanded = false
                            },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            icon = { Icon(imageVector = Icons.Default.LocalOffer, contentDescription = "Daftar Promosi UMKM") },
                            text = { Text("Daftar Promosi UMKM") }
                        )

                        ExtendedFloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.Category.createRoute(umkmId))
                                isFabExpanded = false
                            },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            icon = { Icon(Icons.Filled.Category, contentDescription = "Tambah Kategori") },
                            text = { Text("Kategori") }
                        )

                        ExtendedFloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.AddProduct.createRoute(umkmId))
                                isFabExpanded = false
                            },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            icon = { Icon(Icons.Filled.AddShoppingCart, contentDescription = "Tambah Produk") },
                            text = { Text("Tambah Produk") }
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    AnimatedContent(targetState = isFabExpanded, label = "") { expanded ->
                        if (expanded) {
                            Icon(Icons.Default.Close, "Tutup")
                        } else {
                            Icon(Icons.Filled.Add, "Tambah")
                        }
                    }
                }
            }
        },
        containerColor = colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(padding)
        ) {
            item {
                MerchantHeader(onBack = { navController.popBackStack() }, navController = navController, umkmId = umkmId)
            }
            item {
                PromoSection(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = colorScheme.surfaceVariant, thickness = 8.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SearchAndFilterSection(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    searchQuery = searchQuery,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = viewModel::onCategorySelected,
                    sortOrder = sortOrder,
                    onSortBestSelling = viewModel::onSortBestSelling,
                    onSortPrice = { viewModel.onSortPrice(sortOrder != SortOrder.PRICE_ASC) }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            if (productGroups.isEmpty() && searchQuery.isNotEmpty()) {
                item {
                    Text(
                        text = "Tidak ada produk yang ditemukan.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        textAlign = TextAlign.Center,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            } else {
                productGroups.forEach { (categoryName, productsInCategory) ->
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = colorScheme.background
                        ) {
                            Text(
                                text = categoryName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    items(productsInCategory, key = { it.id }) { product ->
                        MyMenuItemCard(
                            product = product,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onEditClick = { /* TODO */ },
                            onDeleteClick = { viewModel.deleteProduct(product) } // Pass the whole product
                        )
                        HorizontalDivider(
                            color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MerchantHeader(
    onBack: () -> Unit,
    navController: NavController,
    umkmId: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_seblak_header),
            contentDescription = "Header Seblak Sendik",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 30.dp, start = 8.dp)
                .clip(CircleShape)
                .background(colorScheme.background.copy(alpha = 0.5f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = colorScheme.primary)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_seblak_sendik),
                        contentDescription = "Logo Seblak Sendik",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Seblak Sendik",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "4,5",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = colorScheme.secondary
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.Star,
                                "Rating",
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Jl. Timor Manis No. 23, Padang...",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("detailscreen/$umkmId") },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Lihat Detail UMKM",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Lihat detail UMKM",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PromoSection(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "PROMO MENARIK",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PromoGreenBg),
                onClick = { /* No action */ }
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PAKET SEBLAK KOMPLIT + ES TEH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Rp 19.000",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "Rp 22.000",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFilterSection(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    categories: List<Category>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    sortOrder: SortOrder,
    onSortBestSelling: () -> Unit,
    onSortPrice: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari Produk...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari Produk",
                    tint = colorScheme.primary
                )
            },
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

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    text = "Semua",
                    isSelected = selectedCategory == null,
                    onClick = { onCategorySelected(null) }
                )
            }

            items(categories) { category ->
                FilterChip(
                    text = category.name,
                    isSelected = selectedCategory == category.name,
                    onClick = { onCategorySelected(category.name) }
                )
            }

            item {
                FilterChip(
                    text = "Terlaris",
                    isSelected = sortOrder == SortOrder.BEST_SELLING,
                    onClick = onSortBestSelling
                )
            }

            item {
                FilterChip(
                    text = "Harga",
                    isSelected = sortOrder == SortOrder.PRICE_ASC || sortOrder == SortOrder.PRICE_DESC,
                    onClick = onSortPrice,
                    trailingIcon = {
                        when (sortOrder) {
                            SortOrder.PRICE_ASC -> Icon(Icons.Default.ArrowUpward, "Harga Naik", modifier = Modifier.size(18.dp))
                            SortOrder.PRICE_DESC -> Icon(Icons.Default.ArrowDownward, "Harga Turun", modifier = Modifier.size(18.dp))
                            else -> Icon(Icons.Default.UnfoldMore, "Urutkan Harga", modifier = Modifier.size(18.dp))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChip(
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