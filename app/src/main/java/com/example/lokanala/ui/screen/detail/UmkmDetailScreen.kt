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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.request.ImageRequest
import com.example.lokanala.R
import com.example.lokanala.data.remote.response_and_request.UMKMDetail
import com.example.lokanala.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UmkmDetailScreen(
    umkmId: Long,
    navController: NavController,
    viewModel: UmkmDetailViewModel = viewModel()
) {
    
    val context = LocalContext.current

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
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            
            val detail = (uiState as? UmkmDetailUiState.Success)?.umkmDetail

            FloatingActionButton(
                onClick = {
                    
                    if (detail != null && !detail.linkLokasi.isNullOrEmpty()) {
                        openGoogleMaps(context, detail.linkLokasi)
                    } else {
                        android.widget.Toast.makeText(context, "Link lokasi tidak tersedia", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                shape = CircleShape,
                containerColor = PrimaryPink,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Buka Google Maps")
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
                coil.compose.AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(detail.gambarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${detail.name} logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surface),
                    placeholder = painterResource(R.drawable.logo_lokanala),
                    error = painterResource(R.drawable.logo_lokanala),
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

                        Text(
                            text = detail.name,
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 20.sp, 
                            color = colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = FilterChipBg)

                        InfoSection(icon = Icons.Default.Description, title = "Deskripsi Singkat", content = detail.description)
                        InfoSection(icon = Icons.Default.LocationOn, title = "Alamat", content = detail.address)
                        InfoSection(icon = Icons.Default.Phone, title = "Kontak", content = detail.contact)

                        detail.promos.forEachIndexed { index, promo ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp),
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

        
        item { Spacer(modifier = Modifier.height(100.dp)) }

        
    }
}


fun openGoogleMaps(context: android.content.Context, url: String?) {
    if (!url.isNullOrEmpty()) {
        try {
            val uri = android.net.Uri.parse(url)
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
            
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            
            val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            context.startActivity(browserIntent)
        }
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