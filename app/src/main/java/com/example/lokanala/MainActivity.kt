package com.example.lokanala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.pref.dataStore
import com.example.lokanala.ui.navigation.AppNavGraph
import com.example.lokanala.ui.theme.LokanalaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

            val context = LocalContext.current

            val userPreference = UserPreference.getInstance(context.dataStore)

            AppNavGraph(
                navController = navController,
                userPreference = userPreference
            )
        }
    }
}