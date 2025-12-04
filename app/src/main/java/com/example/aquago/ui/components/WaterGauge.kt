package com.example.aquago.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaterGauge(
    currentLiters: Float,
    maxLiters: Float,
    size: Dp = 250.dp
) {
    // calculo porcentaje (0.0 a 1.0)
    val percentage = (currentLiters / maxLiters).coerceIn(0f, 1f)

    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000), label = "WaterGaugeAnimation"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Round)
            )

            val color = when {
                percentage > 1f -> Color(0xFFE53935)
                percentage > 0.8f -> Color(0xFFFFCC80)
                else -> Color(0xFF2196F3)
            }

            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * animatedPercentage,
                useCenter = false,
                style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${currentLiters.toInt()} L",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "de ${maxLiters.toInt()} L",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}