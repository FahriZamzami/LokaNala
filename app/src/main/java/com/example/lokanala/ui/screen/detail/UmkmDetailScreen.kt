package com.example.lokanala.ui.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.model.UMKMDetail
import com.example.lokanala.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UmkmDetailScreen(
    umkmId: Long,
    navController: NavController,
    viewModel: UmkmDetailViewModel = viewModel()
) {
    LaunchedEffect(umkmId) {
        viewModel.getUmkmDetailById(umkmId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail UMKM", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPink,
                    titleContentColor = colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Buka Maps */ },
                shape = CircleShape,
                containerColor = PrimaryPink,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Lihat Lokasi")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorScheme.background)
        ) {
            when (val state = uiState) {
                is UmkmDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UmkmDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UmkmDetailUiState.Success -> {
                    UmkmDetailContent(detail = state.umkmDetail)
                }
            }
        }
    }
}

@Composable
private fun UmkmDetailContent(detail: UMKMDetail) {
    val colorScheme = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = detail.logoRes),
                    contentDescription = "${detail.name} logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surface)
                        .padding(8.dp)
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryPink)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nama UMKM:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = TextGrey
                            )
                            Text(
                                text = detail.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colorScheme.onSurface
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = FilterChipBg)

                        InfoSection(
                            icon = Icons.Default.Description,
                            title = "Deskripsi Singkat:",
                            content = detail.description
                        )
                        InfoSection(
                            icon = Icons.Default.LocationOn,
                            title = "Alamat:",
                            content = detail.address
                        )
                        InfoSection(
                            icon = Icons.Default.Phone,
                            title = "Kontak:",
                            content = detail.contact
                        )
                        InfoSection(
                            icon = Icons.Default.Campaign,
                            title = "Daftar Promosi Aktif:",
                            content = null
                        )

                        detail.promos.forEachIndexed { index, promo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = if (index == 0) Icons.Default.Campaign else Icons.Default.Redeem,
                                    contentDescription = null,
                                    tint = PrimaryPink,
                                    modifier = Modifier.size(18.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = promo, fontSize = 14.sp, color = colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun InfoSection(
    icon: ImageVector,
    title: String,
    content: String?
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = PrimaryPink)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorScheme.onSurface)
        }
        if (content != null) {
            Text(
                text = content,
                fontSize = 14.sp,
                color = TextGrey,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}