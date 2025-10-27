package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.R
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.SearchBarBg
import com.example.lokanala.ui.theme.TextGreyLight

@Composable
fun HomeSearchBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = SearchBarBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_lokanala),
                contentDescription = "Logo",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextGreyLight
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search",
                color = TextGreyLight,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun HomeSearchBarPreview() {
    LokanalaTheme {
        HomeSearchBar(modifier = Modifier.padding(16.dp))
    }
}