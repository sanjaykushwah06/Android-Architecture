/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Custom face icon component for face authentication UI
 */

package com.iiwa.biometric

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FaceIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        drawFaceIcon(color)
    }
}

private fun DrawScope.drawFaceIcon(color: Color) {
    val strokeWidth = size.width * 0.08f
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val radius = size.width * 0.4f

    // Face outline (circle)
    drawCircle(
        color = color,
        radius = radius,
        center = Offset(centerX, centerY),
        style = Stroke(width = strokeWidth)
    )

    // Left eye
    val eyeRadius = size.width * 0.05f
    val eyeOffsetX = size.width * 0.15f
    val eyeOffsetY = size.height * 0.15f
    
    drawCircle(
        color = color,
        radius = eyeRadius,
        center = Offset(centerX - eyeOffsetX, centerY - eyeOffsetY)
    )

    // Right eye
    drawCircle(
        color = color,
        radius = eyeRadius,
        center = Offset(centerX + eyeOffsetX, centerY - eyeOffsetY)
    )

    // Nose (simple line)
    val noseStartY = centerY - size.height * 0.05f
    val noseEndY = centerY + size.height * 0.05f
    
    drawLine(
        color = color,
        start = Offset(centerX, noseStartY),
        end = Offset(centerX, noseEndY),
        strokeWidth = strokeWidth
    )

    // Mouth (arc)
    val mouthPath = Path().apply {
        val mouthCenterY = centerY + size.height * 0.15f
        val mouthWidth = size.width * 0.2f
        
        moveTo(centerX - mouthWidth, mouthCenterY)
        quadraticTo(
            centerX, mouthCenterY + size.height * 0.08f,
            centerX + mouthWidth, mouthCenterY
        )
    }
    
    drawPath(
        path = mouthPath,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}
