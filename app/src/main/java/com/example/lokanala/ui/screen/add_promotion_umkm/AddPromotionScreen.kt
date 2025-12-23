package com.example.lokanala.ui.screen.add_promotion_umkm

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lokanala.ui.screen.promotion_umkm.Promotion
import com.example.lokanala.ui.screen.promotion_umkm.PromotionViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPromotionScreen(
    navController: NavController,
    umkmId: Int,
    addPromoViewModel: AddPromoViewModel
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    
    val calendar = Calendar.getInstance()
    val todayString = "%04d-%02d-%02d".format(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    
    var titleText by remember { mutableStateOf("") }
    var detailText by remember { mutableStateOf("") }
    var termsText by remember { mutableStateOf("") }
    var usageText by remember { mutableStateOf("") }
    var startText by remember { mutableStateOf(todayString) }
    var endText by remember { mutableStateOf(todayString) }

    val state by addPromoViewModel.state.collectAsState()

    
    val isFormValid = titleText.isNotBlank() &&
            detailText.isNotBlank() &&
            termsText.isNotBlank() &&
            usageText.isNotBlank() &&
            startText.isNotBlank() &&
            endText.isNotBlank()

    fun showDatePicker(initialDate: String, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val parts = initialDate.split("-")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val day = parts[2].toInt()
            calendar.set(year, month, day)
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dateStr = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                onDateSelected(dateStr)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tambah Promo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.primary
                        )
                    }
                }
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = detailText,
                onValueChange = { detailText = it },
                label = { Text("Detail") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
            )

            OutlinedTextField(
                value = termsText,
                onValueChange = { termsText = it },
                label = { Text("Syarat Penggunaan") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
            )

            OutlinedTextField(
                value = usageText,
                onValueChange = { usageText = it },
                label = { Text("Cara Penggunaan") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
            )

            PromotionDateField("Start", startText) {
                showDatePicker(startText) { startText = it }
            }

            PromotionDateField("End", endText) {
                showDatePicker(endText) { endText = it }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) { Text("Batal") }

                Button(
                    onClick = {
                        addPromoViewModel.addPromo(
                            umkmId,
                            titleText,
                            detailText,
                            termsText,
                            usageText,
                            startText,
                            endText
                        )
                    },
                    enabled = isFormValid,
                    modifier = Modifier.weight(1f)
                ) { Text("Simpan") }
            }

            when (state) {
                is AddPromoState.Loading -> Text("Loading...", color = colorScheme.primary)
                is AddPromoState.Success -> {
                    LaunchedEffect(state) { navController.popBackStack() }
                }
                is AddPromoState.Error -> Text(
                    (state as AddPromoState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                else -> {}
            }
        }
    }
}

@Composable
fun PromotionDateField(label: String, dateText: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(dateText)
            }
        }
    }
}