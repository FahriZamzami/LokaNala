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
    val typography = MaterialTheme.typography

    val ratings = reviews.map { it.rating }
    val average = if (ratings.isNotEmpty()) ratings.average() else 0.0
    val counts = (1..5).associateWith { r -> ratings.count { it == r } }
    val maxCount = counts.values.maxOrNull()?.toFloat() ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.45f)
                ) {
                    Text(
                        text = String.format("%.1f", average),
                        style = typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = colorScheme.onSurface
                        )
                    )

                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(5) { i ->
                            val tint = if (i < average.roundToInt())
                                colorScheme.secondary
                            else
                                colorScheme.outlineVariant
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = "${ratings.size} ulasan",
                        style = typography.bodySmall.copy(
                            color = colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(0.55f)
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (5 downTo 1).forEach { star ->
                        val count = counts[star] ?: 0
                        val progress = if (maxCount > 0) count.toFloat() / maxCount else 0f

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "$star",
                                style = typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurface
                                )
                            )

                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = when {
                                    star >= 4 -> colorScheme.primary
                                    star == 3 -> colorScheme.tertiary
                                    else -> colorScheme.secondaryContainer
                                },
                                trackColor = colorScheme.surfaceVariant
                            )

                            Text(
                                "$count",
                                style = typography.bodySmall.copy(
                                    color = colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}