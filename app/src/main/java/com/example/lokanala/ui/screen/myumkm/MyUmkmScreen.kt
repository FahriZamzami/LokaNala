package com.example.lokanala.ui.screen.myumkm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.ui.components.MyUmkmCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.screen.addumkm.AuthViewModel

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUmkmScreen(
    onBack: () -> Unit,
    navController: NavController,
    viewModel: MyUmkmViewModel = viewModel(),
    authViewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Loading jika user belum ada
    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Memuat data user...")
        }
        return
    }

    // Load UMKM saat user tersedia
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            viewModel.loadMyUmkm(user.id_user)
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedUmkmId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("UMKM Saya", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AddUmkm.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah UMKM")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        if (uiState.myUmkmList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada UMKM")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(12.dp)
            ) {
                items(uiState.myUmkmList, key = { it.id_umkm }) { umkm ->
                    MyUmkmCard(
                        umkm = umkm,
                        onEditClick = { navController.navigate(Screen.EditUmkm.createRoute(umkm.id_umkm)) },
                        onDeleteClick = {
                            selectedUmkmId = umkm.id_umkm
                            showDeleteDialog = true
                        },
                        onMerchantClick = { navController.navigate(Screen.MyMerchant.createRoute(umkm.id_umkm)) }
                    )
                }
            }
        }
    }

    if (showDeleteDialog && selectedUmkmId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedUmkmId = null },
            title = { Text("Hapus UMKM") },
            text = { Text("Apakah Anda yakin ingin menghapus UMKM ini?") },
            confirmButton = {
                TextButton(onClick = {
                    val idToDelete = selectedUmkmId ?: return@TextButton
                    currentUser?.let { user ->
                        showDeleteDialog = false
                        selectedUmkmId = null
                        viewModel.deleteUmkm(idToDelete, user.id_user) { success, msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    }
                }) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedUmkmId = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
