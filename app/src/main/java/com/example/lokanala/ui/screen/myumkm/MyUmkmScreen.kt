package com.example.lokanala.ui.screen.myumkm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.components.MyUmkmCard
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.LokanalaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUmkmScreen(
    modifier: Modifier = Modifier,
    viewModel: MyUmkmViewModel = viewModel(),
    onBack: () -> Unit,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MY UMKM",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onPrimary,
                    navigationIconContentColor = colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddUmkm.route) },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Tambah UMKM")
            }
        },
        containerColor = colorScheme.background
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.myUmkmList, key = { it.id }) { umkm ->
                MyUmkmCard(
                    umkm = umkm,
                    onEditClick = { /* TODO: Navigasi ke edit */ },
                    onDeleteClick = { /* TODO: Tampilkan dialog hapus */ },
                    onMerchantClick = {
                        navController.navigate(Screen.MyMerchant.createRoute(umkm.id))
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyUmkmScreenPreview() {
    LokanalaTheme(darkTheme = false) {
        MyUmkmScreen(onBack = {}, navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun MyUmkmScreenDarkPreview() {
    LokanalaTheme(darkTheme = true) {
        MyUmkmScreen(onBack = {}, navController = rememberNavController())
    }
}