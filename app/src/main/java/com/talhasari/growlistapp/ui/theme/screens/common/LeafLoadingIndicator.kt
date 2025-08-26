package com.talhasari.growlistapp.ui.theme.screens.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

@Composable
fun LeafLoadingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "leaf-loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val color = MaterialTheme.colorScheme.primary

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.size(48.dp)) {
            rotate(rotation) {
                val path = Path().apply {
                    moveTo(center.x, center.y - size.minDimension / 2)
                    quadraticBezierTo(
                        center.x + size.minDimension / 1.5f, center.y,
                        center.x, center.y + size.minDimension / 2
                    )
                    quadraticBezierTo(
                        center.x - size.minDimension / 1.5f, center.y,
                        center.x, center.y - size.minDimension / 2
                    )
                    close()
                }
                drawPath(path, color = color)
                drawLine(
                    color = color,
                    start = Offset(center.x, center.y - size.minDimension / 4),
                    end = Offset(center.x, center.y + size.minDimension / 2),
                    strokeWidth = 3f
                )
            }
        }
    }
}