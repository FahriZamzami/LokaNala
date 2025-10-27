package com.example.lokanala.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.PromoPinkText
import com.example.lokanala.ui.theme.TextGreyLight

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = PromoPinkText, // Warna ikon pink
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        trailingText?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = TextGreyLight
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileMenuItemPreview() {
    LokanalaTheme {
        Column {
            ProfileMenuItem(icon = Icons.AutoMirrored.Filled.ListAlt, text = "My UMKM")
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                text = "Ketentuan & privasi",
                trailingText = "Setujui"
            )
        }
    }
}