package com.example.lokanala.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokanala.ui.screen.rating.Review
import kotlin.math.roundToInt

@Composable
fun RatingOverview(reviews: List<Review>) {
    val colorScheme = MaterialTheme.colorScheme

    val ratings = reviews.map { it.rating }
    val average = if (ratings.isNotEmpty()) ratings.average() else 0.0
    val counts = (1..5).associateWith { r -> ratings.count { it == r } }
    val maxCount = counts.values.maxOrNull()?.toFloat() ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.2f", average),
                        fontWeight = FontWeight.Black,
                        fontSize = 40.sp,
                        color = colorScheme.onBackground
                    )
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(5) { i ->
                            val tint = if (i < average.roundToInt())
                                colorScheme.secondary
                            else
                                colorScheme.surfaceVariant
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "${ratings.size} ratings",
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (5 downTo 1).forEach { star ->
                        val count = counts[star] ?: 0
                        val progress = if (maxCount > 0) count.toFloat() / maxCount else 0f

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("$star", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = if (star >= 4) colorScheme.primary else colorScheme.primaryContainer,
                                trackColor = colorScheme.surfaceVariant
                            )
                            Text(
                                "$count",
                                fontSize = 14.sp,
                                color = colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}
