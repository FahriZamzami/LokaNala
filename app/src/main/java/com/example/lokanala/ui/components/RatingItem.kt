package com.example.lokanala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.R
import com.example.lokanala.model.Review
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.StarYellow
import com.example.lokanala.ui.theme.TextGreyLight

@Composable
fun RatingItem(
    review: Review,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = review.profilePicRes),
                contentDescription = review.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Row {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = StarYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = review.date,
                fontSize = 12.sp,
                color = TextGreyLight
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = review.comment,
            fontSize = 13.sp,
            color = TextGreyLight,
            lineHeight = 18.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatingItemPreview() {
    LokanalaTheme {
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
