package com.example.lokanala.ui.screen.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.components.HomeSearchBar
import com.example.lokanala.ui.components.LokanalaBottomBar
import com.example.lokanala.ui.components.UmkmCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    val fusedLocationClient = remember {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    }

    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            
            if (androidx.core.app.ActivityCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                androidx.core.app.ActivityCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        viewModel.updateLocationName(context, it.latitude, it.longitude)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshData()
        
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            HomeTopBar(
                navController = navController,
                locationName = uiState.userLocation 
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeSearchBar(
                    searchQuery = viewModel.searchQuery.collectAsState().value,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged
                )
                FilterChips(viewModel = viewModel)
            }

            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                when {
                    
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(top = 50.dp)
                                .align(Alignment.Center)
                        )
                    }

                    
                    uiState.errorMessage != null -> {
                        Text(
                            text = uiState.errorMessage ?: "Terjadi kesalahan",
                            color = colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 50.dp)
                                .align(Alignment.Center)
                        )
                    }

                    
                    uiState.umkmList.isEmpty() -> {
                        Text(
                            text = "Belum ada data UMKM",
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(top = 50.dp)
                                .align(Alignment.Center)
                        )
                    }

                    
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.filteredUmkmList, key = { it.id }) { umkm ->
                                UmkmCard(
                                    umkm = umkm,
                                    onClick = {
                                        navController.navigate(Screen.Merchant.createRoute(umkm.id.toLong()))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            LokanalaBottomBar(
                navController = navController,
                currentRoute = Screen.Home.route
            )
        }
    }
}



@Composable
private fun HomeTopBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    locationName: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = "Lokasi",
                tint = colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = locationName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                navController.navigate(Screen.Notification.route)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifikasi",
                    tint = colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChips(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedKategori by viewModel.selectedKategori.collectAsState()
    val kategoriList by viewModel.uiState.collectAsState()
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        
        item {
            FilterChipItem(
                text = "Semua",
                isSelected = selectedFilter == FilterType.TIPE_UMKM && selectedKategori == null,
                onClick = { viewModel.setKategori(null) }
            )
        }
        
        
        item {
            KategoriDropdownChip(
                viewModel = viewModel,
                kategoriList = kategoriList.kategoriUmkmList,
                selectedKategori = selectedKategori
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val (bgColor, textColor, border) = if (isSelected) {
        Triple(
            colorScheme.primary.copy(alpha = 0.15f),
            colorScheme.primary,
            BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f))
        )
    } else {
        Triple(
            colorScheme.surfaceVariant.copy(alpha = 0.5f),
            colorScheme.onSurfaceVariant,
            BorderStroke(0.dp, Color.Transparent)
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = border,
        onClick = onClick
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun KategoriDropdownChip(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    kategoriList: List<String>,
    selectedKategori: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    
    
    val isSelected = selectedFilter == FilterType.TIPE_UMKM || selectedKategori != null
    
    Box(modifier = modifier) {
        Surface(
            color = if (isSelected) {
                colorScheme.primary.copy(alpha = 0.15f)
            } else {
                colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
            shape = RoundedCornerShape(20.dp),
            border = if (isSelected) {
                BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f))
            } else {
                BorderStroke(0.dp, Color.Transparent)
            },
            onClick = { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = selectedKategori ?: "Tipe UMKM",
                    color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            
            if (kategoriList.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Tidak ada kategori") },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                kategoriList.forEach { kategori ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                text = kategori,
                                fontWeight = if (selectedKategori == kategori) FontWeight.SemiBold else FontWeight.Normal
                            ) 
                        },
                        onClick = {
                            viewModel.setKategori(kategori)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LokanalaTheme {
        HomeScreen(navController = rememberNavController())
    }
}