package com.example.lokanala.ui.screen.edit_promotion_umkm

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lokanala.ui.screen.promotion_umkm.MonthConverter
import com.example.lokanala.ui.screen.promotion_umkm.PromotionViewModel

import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPromotionScreen(
    navController: NavController,
    promotionId: Int,
    promotionViewModel: PromotionViewModel
) {
    val context = LocalContext.current
    val promotion = promotionViewModel.getPromotionById(promotionId)
    val colorScheme = MaterialTheme.colorScheme

    if (promotion == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Promotion not found", color = colorScheme.onSurfaceVariant)
        }
        return
    }

    var titleText by remember { mutableStateOf(promotion.title) }
    var detailText by remember { mutableStateOf(promotion.detail) }
    var startText by remember { mutableStateOf(promotion.startDate) }
    var endText by remember { mutableStateOf(promotion.endDate) }

    fun showDatePicker(initialDate: String, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val dateParts = initialDate.split(" ")

        if (dateParts.size == 3) {
            val day = dateParts[0].toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH)
            val month = try {
                MonthConverter.getMonthIndex(dateParts[1])
            } catch (_: Exception) {
                calendar.get(Calendar.MONTH)
            }
            val year = dateParts[2].toIntOrNull() ?: calendar.get(Calendar.YEAR)
            calendar.set(year, month, day)
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth ${MonthConverter.getMonthName(month)} $year"
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Promotion", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary.copy(alpha = 0.3f))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = colorScheme.primary.copy(alpha = 0.08f),
                        radius = 450f,
                        center = Offset(size.width * 0.9f, size.height * 0.1f)
                    )
                    drawCircle(
                        color = colorScheme.primary.copy(alpha = 0.05f),
                        radius = 350f,
                        center = Offset(size.width * 0.1f, size.height * 0.9f)
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.onSurfaceVariant
                    )
                )

                // Detail
                OutlinedTextField(
                    value = detailText,
                    onValueChange = { detailText = it },
                    label = { Text("Detail") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.onSurfaceVariant
                    )
                )

                // Start & End Dates
                DateField(label = "Start Date", dateText = startText) {
                    showDatePicker(startText) { selected -> startText = selected }
                }
                DateField(label = "End Date", dateText = endText) {
                    showDatePicker(endText) { selected -> endText = selected }
                }

                // Action Buttons: Delete | Cancel | Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Delete
                    OutlinedButton(
                        onClick = {
                            promotionViewModel.deletePromotion(promotion.id)
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = SolidColor(colorScheme.error)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.error
                        )
                    ) {
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }

                    // Cancel
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = SolidColor(colorScheme.primary)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.primary
                        )
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    // Save
                    Button(
                        onClick = {
                            val updated = promotion.copy(
                                title = titleText,
                                detail = detailText,
                                startDate = startText,
                                endDate = endText
                            )
                            promotionViewModel.updatePromotion(updated)
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DateField(label: String, dateText: String, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.surfaceVariant)
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(dateText, color = colorScheme.onSurface)
            }
        }
    }
}

