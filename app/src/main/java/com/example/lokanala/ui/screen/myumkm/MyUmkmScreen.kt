package com.example.lokanala.ui.screen.myumkm

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.lokanala.ui.ViewModelFactory 
import com.example.lokanala.ui.components.MyUmkmCard
import com.example.lokanala.ui.navigation.Screen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.repeatOnLifecycle
import com.example.lokanala.data.remote.response_and_request.UmkmResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUmkmScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current

    val viewModel: MyUmkmViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
            Log.d("MyUmkmScreen", "Layar Resume: Memperbarui data...")
            viewModel.loadMyUmkm()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var showDeleteDialog by remember { mutableStateOf(false) }
    var umkmToDelete by remember { mutableStateOf<UmkmResponse?>(null) }

    if (showDeleteDialog && umkmToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus UMKM") },
            text = { Text("Apakah Anda yakin ingin menghapus '${umkmToDelete?.nama}'? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        umkmToDelete?.let { viewModel.deleteUmkm(it.id) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

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
            

            if (uiState.isLoading) {
                
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                
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
                
                Text(
                    text = "Anda belum memiliki UMKM.\nSilakan tambah UMKM baru.",
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                
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
                                Log.d("NAV_DEBUG", "Klik Edit pada: ${umkm.nama}, ID: ${umkm.id}")
                                navController.navigate(Screen.EditUmkm.createRoute(umkm.id))
                            },
                            onDeleteClick = {
                                
                                umkmToDelete = umkm
                                showDeleteDialog = true
                            },
                            onMerchantClick = {
                                navController.navigate(Screen.MyMerchant.createRoute(umkm.id))
                            }
                        )
                    }
                }
            }
        }
    }
}