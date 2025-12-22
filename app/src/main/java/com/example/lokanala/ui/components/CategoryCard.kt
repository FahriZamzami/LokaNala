package com.example.lokanala.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lokanala.data.remote.response_and_request.CategoryItem

@Composable
fun CategoryCard(
    category: CategoryItem,
    isDragging: Boolean = false,
    isTarget: Boolean = false,
    dragOffset: androidx.compose.ui.unit.Dp = 0.dp,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val animatedOffset by animateDpAsState(dragOffset, tween(if (isDragging) 0 else 300))
    val scale by animateFloatAsState(if (isDragging) 1.05f else 1f, tween(200))
    val targetAlpha by animateFloatAsState(if (isTarget) 0.6f else 1f, tween(150))
    
    Card(
        elevation = CardDefaults.cardElevation(if (isDragging) 12.dp else if (isTarget) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDragging -> colorScheme.primaryContainer.copy(alpha = 0.7f)
                isTarget -> colorScheme.secondaryContainer.copy(alpha = 0.5f)
                else -> colorScheme.surface
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedOffset)
            .scale(scale)
            .alpha(targetAlpha)
            .pointerInput(category.id) {
                detectDragGestures(
                    onDragStart = { _: Offset -> onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDrag = { _, dragAmount -> onDrag(dragAmount.y) }
                )
            }
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.SwapHoriz,
                "Drag",
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp).padding(end = 12.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                category.description?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = colorScheme.error)
                }
            }
        }
    }
}