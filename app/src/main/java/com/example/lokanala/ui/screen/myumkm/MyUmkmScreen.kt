package com.example.lokanala.ui.screen.myumkm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.ui.ViewModelFactory // Import Factory baru
import com.example.lokanala.ui.components.MyUmkmCard
import com.example.lokanala.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUmkmScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current

    // PERBAIKAN: Menggunakan ViewModelFactory.getInstance(context)
    // Ini mencegah pembuatan DataStore ganda
    val viewModel: MyUmkmViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )

    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "UMKM Saya",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddUmkm.route) },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah UMKM")
            }
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- LOGIKA TAMPILAN BERDASARKAN STATE ---

            if (uiState.isLoading) {
                // 1. Loading State
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                // 2. Error State
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Terjadi kesalahan",
                        color = colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadMyUmkm() }) {
                        Text("Coba Lagi")
                    }
                }
            } else if (uiState.myUmkmList.isEmpty()) {
                // 3. Empty State (Data Kosong)
                Text(
                    text = "Anda belum memiliki UMKM.\nSilakan tambah UMKM baru.",
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // 4. Success State (Tampilkan List)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.myUmkmList, key = { it.id }) { umkm ->
                        MyUmkmCard(
                            umkm = umkm,
                            onEditClick = {
                                // TODO: Implementasi navigasi ke halaman Edit UMKM
                            },
                            onDeleteClick = {
                                // TODO: Implementasi logika hapus UMKM di ViewModel
                            },
                            onMerchantClick = {
                                // Navigasi ke Halaman Kelola Merchant
                                navController.navigate(Screen.MyMerchant.createRoute(umkm.id))
                            }
                        )
                    }
                }
            }
        }
    }
}