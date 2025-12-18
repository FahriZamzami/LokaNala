package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.R
import com.example.lokanala.ui.theme.LokanalaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    onSearchQueryChanged: (String) -> Unit = {},
    placeholder: String = "Cari UMKM atau produk..."
) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_lokanala),
                    contentDescription = "Logo Lokanala",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = placeholder,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = colorScheme.primary
            )
        },
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = colorScheme.surfaceVariant,
            focusedContainerColor = colorScheme.surfaceVariant
        ),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun HomeSearchBarPreview() {
    LokanalaTheme {
        HomeSearchBar(modifier = Modifier.padding(16.dp))
    }
}