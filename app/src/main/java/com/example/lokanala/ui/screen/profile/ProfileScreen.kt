package com.example.lokanala.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.R
import com.example.lokanala.ui.components.LokanalaBottomBar
import com.example.lokanala.ui.components.ProfileMenuItem
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.PromoPinkBg
import com.example.lokanala.ui.theme.TextGreyLight

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            ProfileTopBar()

            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { UserInfoCard() }
                item { AccountSection(navController = navController) }
                item { InfoSection() }
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
                currentRoute = Screen.Profile.route
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    val colorScheme = MaterialTheme.colorScheme

    TopAppBar(
        title = {
            Text(
                text = "Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colorScheme.onBackground
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.background,
            titleContentColor = colorScheme.onBackground
        )
    )
}

@Composable
private fun UserInfoCard() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_gracia_liora),
                contentDescription = "Gracia Liora",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gracia Liora",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lioragrace@gmail.com",
                    fontSize = 14.sp,
                    color = TextGreyLight
                )
                Text(
                    text = "+6289786534217",
                    fontSize = 14.sp,
                    color = TextGreyLight
                )
            }
            IconButton(onClick = { /* TODO: Edit Profile */ }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Profile",
                    tint = colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun AccountSection(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        Text(
            text = "Account",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.ListAlt,
                    text = "My UMKM",
                    onClick = { navController.navigate(Screen.MyUmkm.route) }
                )
                ProfileMenuItem(
                    icon = Icons.Filled.Discount,
                    text = "My UMKM Promotion",
                    onClick = { navController.navigate("promotion") }
                )
                ProfileMenuItem(icon = Icons.Filled.StarOutline, text = "Ulasan")
                ProfileMenuItem(icon = Icons.AutoMirrored.Filled.HelpOutline, text = "Pusat bantuan")
                ProfileMenuItem(icon = Icons.Filled.LockOpen, text = "Keamanan akun")
                ProfileMenuItem(icon = Icons.AutoMirrored.Filled.Logout, text = "Log out")
            }
        }
    }
}

@Composable
private fun InfoSection() {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        Text(
            text = "Info Lainnya",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Filled.Info,
                    text = "Ketentuan & privasi",
                    trailingText = "Setujui"
                )
                ProfileMenuItem(
                    icon = Icons.Filled.StarOutline,
                    text = "Beri rating",
                    trailingText = "v 1.0.1"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    LokanalaTheme {
        ProfileScreen(navController = rememberNavController())
    }
}