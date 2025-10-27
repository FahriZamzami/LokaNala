package com.example.lokanala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.navigation.AppNavGraph
import com.example.lokanala.ui.theme.LokanalaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // ✅ Modern edge-to-edge layout
        setContent {
            LokaNalaApp()
        }
    }
}

@Composable
private fun LokaNalaApp() {
    LokanalaTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            AppNavGraph(navController = navController)
        }
    }
}
