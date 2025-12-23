package com.example.lokanala.ui.screen.edit_promotion_umkm

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lokanala.ui.screen.promotion_umkm.PromotionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPromotionScreen(
    navController: NavController,
    promotionId: Int,
    umkmId: Int,
    promotionViewModel: PromotionViewModel,
    editPromoViewModel: EditPromoViewModel
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val promotion = promotionViewModel.getPromotionById(promotionId)
    if (promotion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Promotion not found", color = colorScheme.onSurfaceVariant)
        }
        return
    }

    
    var titleText by remember { mutableStateOf(promotion.title) }
    var detailText by remember { mutableStateOf(promotion.detail) }
    var termsText by remember { mutableStateOf(promotion.syarat ?: "") }
    var usageText by remember { mutableStateOf(promotion.cara ?: "") }
    var startText by remember { mutableStateOf(promotion.startDate) }
    var endText by remember { mutableStateOf(promotion.endDate) }

    
    val isFormValid = titleText.isNotBlank() &&
            detailText.isNotBlank() &&
            termsText.isNotBlank() &&
            usageText.isNotBlank() &&
            startText.isNotBlank() &&
            endText.isNotBlank()

    val state by editPromoViewModel.state.collectAsState()

    
    fun showDatePicker(initialDate: String, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val parts = initialDate.split(" ")
        if (parts.size == 3) {
            val day = parts[0].toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH)
            val month = MonthConverter.getMonthIndex(parts[1])
            val year = parts[2].toIntOrNull() ?: calendar.get(Calendar.YEAR)
            calendar.set(year, month, day)
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected("$dayOfMonth ${MonthConverter.getMonthName(month)} $year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Promo", fontWeight = FontWeight.Bold) },
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

        var showDeleteDialog by remember { mutableStateOf(false) }

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
            if (titleText.isBlank()) Text("Title wajib diisi", color = colorScheme.error)

            OutlinedTextField(
                value = detailText,
                onValueChange = { detailText = it },
                label = { Text("Detail") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
            )
            if (detailText.isBlank()) Text("Detail wajib diisi", color = colorScheme.error)

            OutlinedTextField(
                value = termsText,
                onValueChange = { termsText = it },
                label = { Text("Syarat Penggunaan") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp)
            )
            if (termsText.isBlank()) Text("Syarat wajib diisi", color = colorScheme.error)

            OutlinedTextField(
                value = usageText,
                onValueChange = { usageText = it },
                label = { Text("Cara Penggunaan") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp)
            )
            if (usageText.isBlank()) Text("Cara penggunaan wajib diisi", color = colorScheme.error)

            DateField(label = "Start Date", dateText = startText) {
                showDatePicker(startText) { startText = it }
            }
            if (startText.isBlank()) Text("Start date wajib diisi", color = colorScheme.error)

            DateField(label = "End Date", dateText = endText) {
                showDatePicker(endText) { endText = it }
            }
            if (endText.isBlank()) Text("End date wajib diisi", color = colorScheme.error)

            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) { Text("Batal") }

                Button(
                    onClick = {
                        val startIso = startText.toBackendISO()
                        val endIso = endText.toBackendISO()

                        editPromoViewModel.updatePromo(
                            promotion.id,
                            titleText,
                            detailText,
                            termsText,
                            usageText,
                            startIso,
                            endIso
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isFormValid,   
                ) { Text("Simpan") }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Hapus Promo?", fontWeight = FontWeight.Bold) },
                    text = { Text("Apakah Anda yakin ingin menghapus promo ini?") },

                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                editPromoViewModel.deletePromo(promotion.id)
                                navController.popBackStack()
                            }
                        ) {
                            Text("Hapus", color = colorScheme.error)
                        }
                    },

                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            when (state) {
                is EditPromoState.Loading -> Text("Loading...", color = colorScheme.primary)
                is EditPromoState.Success -> {
                    LaunchedEffect(state) { navController.popBackStack() }
                }
                is EditPromoState.Error -> Text(
                    (state as EditPromoState.Error).message,
                    color = colorScheme.error
                )
                else -> {}
            }
        }
    }
}


fun String.toBackendISO(): String {
    return try {
        val sdfInput = SimpleDateFormat("d MMMM yyyy", Locale("id"))
        val date = sdfInput.parse(this)
        SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'", Locale.getDefault()).format(date!!)
    } catch (e: Exception) {
        this
    }
}

@Composable
fun DateField(label: String, dateText: String, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Column {
        Text(label, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.surfaceVariant)
                .clickable { onClick() }
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(dateText)
            }
        }
    }
}