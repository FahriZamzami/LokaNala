package com.example.lokanala.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.BottomBarSelected
import com.example.lokanala.ui.theme.BottomBarUnselected

@Composable
fun LokanalaBottomBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentRoute: String?
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // PERBAIKAN: BottomAppBar tidak punya `navigationIcon` atau `actions`.
        // Sebagai gantinya, kita gunakan Row untuk menata ikon secara manual.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround // Menata ikon secara merata
        ) {
            // --- Tombol Home ---
            IconButton(onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentRoute == Screen.Home.route) BottomBarSelected else BottomBarUnselected
                )
            }

            // --- Tombol Profile ---
            IconButton(onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (currentRoute == Screen.Profile.route) BottomBarSelected else BottomBarUnselected
                )
            }
        }
    }
}
