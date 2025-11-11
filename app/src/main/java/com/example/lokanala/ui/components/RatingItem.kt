package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.R
import com.example.lokanala.model.Review
import com.example.lokanala.ui.theme.LokanalaTheme

@Composable
fun RatingItem(
    review: Review,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = review.profilePicRes),
                contentDescription = review.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.name,
                    style = typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = colorScheme.secondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Text(
                text = review.date,
                style = typography.bodySmall.copy(
                    color = colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = review.comment,
            style = typography.bodySmall.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(start = 4.dp, end = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatingItemPreview() {
    LokanalaTheme(darkTheme = false) {
        RatingItem(
            review = Review(
                id = 1,
                name = "Ratna Solihin",
                rating = 5,
                date = "10 Oktober 2025",
                comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla sem lectus, mattis eu justo sed, maximus faucibus lectus.",
                profilePicRes = R.drawable.img_ratna_solihin
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatingItemPreviewDark() {
    LokanalaTheme(darkTheme = true) {
        RatingItem(
            review = Review(
                id = 1,
                name = "Ratna Solihin",
                rating = 4,
                date = "10 Oktober 2025",
                comment = "Bagus banget produknya! Tapi pengiriman agak lama.",
                profilePicRes = R.drawable.img_ratna_solihin
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}