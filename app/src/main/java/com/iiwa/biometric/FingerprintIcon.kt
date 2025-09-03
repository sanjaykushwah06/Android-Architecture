/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Custom fingerprint icon component with scalable canvas drawing
 */

package com.iiwa.biometric

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FingerprintIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        drawFingerprintPattern(color)
    }
}

private fun DrawScope.drawFingerprintPattern(color: Color) {
    val strokeWidth = size.width * 0.04f
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    
    // Outer curves
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        size = Size(
            width = size.width * 0.8f,
            height = size.height * 0.8f
        ),
        topLeft = Offset(
            x = centerX - (size.width * 0.4f),
            y = centerY - (size.height * 0.4f)
        )
    )
    
    // Middle curves
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        size = Size(
            width = size.width * 0.6f,
            height = size.height * 0.6f
        ),
        topLeft = Offset(
            x = centerX - (size.width * 0.3f),
            y = centerY - (size.height * 0.3f)
        )
    )
    
    // Inner curves
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        size = Size(
            width = size.width * 0.4f,
            height = size.height * 0.4f
        ),
        topLeft = Offset(
            x = centerX - (size.width * 0.2f),
            y = centerY - (size.height * 0.2f)
        )
    )
    
    // Side lines
    drawLine(
        color = color,
        start = Offset(
            x = centerX - (size.width * 0.3f),
            y = centerY + (size.height * 0.1f)
        ),
        end = Offset(
            x = centerX - (size.width * 0.3f),
            y = centerY + (size.height * 0.3f)
        ),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
    
    drawLine(
        color = color,
        start = Offset(
            x = centerX + (size.width * 0.3f),
            y = centerY + (size.height * 0.1f)
        ),
        end = Offset(
            x = centerX + (size.width * 0.3f),
            y = centerY + (size.height * 0.3f)
        ),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
    
    // Center line
    drawLine(
        color = color,
        start = Offset(
            x = centerX,
            y = centerY + (size.height * 0.05f)
        ),
        end = Offset(
            x = centerX,
            y = centerY + (size.height * 0.25f)
        ),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}
