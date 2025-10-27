package com.example.lokanala.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.ui.theme.TextGrey

// PERBAIKAN: Membuat komponen baru khusus untuk daftar info di detail promo
@Composable
fun InfoSectionCard(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEachIndexed { index, item ->
                Row {
                    Text("${index + 1}. ", fontSize = 14.sp, color = TextGrey)
                    Text(item, fontSize = 14.sp, color = TextGrey, lineHeight = 20.sp)
                }
            }
        }
    }
}
