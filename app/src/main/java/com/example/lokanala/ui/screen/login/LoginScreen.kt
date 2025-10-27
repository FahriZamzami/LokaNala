package com.example.lokanala.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.navigation.Screen
import com.example.lokanala.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    // 🎨 Warna tema
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // 🌸 Latar dekoratif di bagian atas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    PromoPinkBg,
                    shape = RoundedCornerShape(bottomStartPercent = 20, bottomEndPercent = 20)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // 🩷 Judul
            Text(
                text = "Login here",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Welcome back you've\nbeen missed!",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = LightText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 🧑 Username Input
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username", color = LightText) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = InputFieldBg,
                    unfocusedContainerColor = InputFieldBg,
                    disabledContainerColor = InputFieldBg,
                    focusedIndicatorColor = PrimaryPink,
                    unfocusedIndicatorColor = FilterChipBorder,
                    cursorColor = PrimaryPink,
                    focusedTextColor = DarkText,
                    unfocusedTextColor = RegularText
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔒 Password Input
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password", color = LightText) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = InputFieldBg,
                    unfocusedContainerColor = InputFieldBg,
                    disabledContainerColor = InputFieldBg,
                    focusedIndicatorColor = PrimaryPink,
                    unfocusedIndicatorColor = FilterChipBorder,
                    cursorColor = PrimaryPink,
                    focusedTextColor = DarkText,
                    unfocusedTextColor = RegularText
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔗 Forgot Password
            Text(
                text = "Forgot your password?",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = LightText,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* TODO: Navigate to Forgot Password */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 🚀 Tombol Sign In
            Button(
                onClick = {
                    viewModel.handleLogin()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Sign in",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ➕ Buat Akun Baru
            Text(
                text = "Create new account",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryPink,
                modifier = Modifier.clickable { /* TODO: Navigate to Register */ }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun LoginScreenPreview() {
    LokanalaTheme {
        LoginScreen(navController = rememberNavController())
    }
}
