package com.example.lokanala.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.R
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.*

import com.example.lokanala.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController
    
) {
    val context = LocalContext.current

    
    val viewModel: LoginViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var passwordVisible by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(uiState.success, uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            if (uiState.success) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding).background(colorScheme.background).verticalScroll(rememberScrollState())) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                Spacer(modifier = Modifier.height(80.dp))
                Image(painter = painterResource(R.drawable.logo_lokanala), contentDescription = "Logo", modifier = Modifier.size(180.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Login here", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary)

                

                Spacer(modifier = Modifier.height(40.dp))
                OutlinedTextField(value = uiState.email, onValueChange = viewModel::onEmailChange, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = uiState.password, onValueChange = viewModel::onPasswordChange, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } })

                Spacer(modifier = Modifier.height(32.dp))

                
                Button(
                    onClick = { viewModel.fetchFcmTokenAndLogin() }, 
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Sign in")
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Create new account",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryPink,
                    modifier = Modifier.clickable {
                        
                        navController.navigate(Screen.Register.route)
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}