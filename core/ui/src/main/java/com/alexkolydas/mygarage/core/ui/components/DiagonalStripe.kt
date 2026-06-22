package com.alexkolydas.mygarage.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.alexkolydas.mygarage.core.ui.theme.GarageAccent

@Composable
fun DiagonalStripe(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
    ) {
        val accent   = GarageAccent.copy(alpha = 0.85f)
        val dark     = Color(0xFF23282D)
        val stripeW  = 9.dp.toPx()
        var isAccent = true
        var x        = -size.height
        while (x < size.width + size.height) {
            val path = Path().apply {
                moveTo(x, size.height)
                lineTo(x + size.height, 0f)
                lineTo(x + size.height + stripeW, 0f)
                lineTo(x + stripeW, size.height)
                close()
            }
            drawPath(path = path, color = if (isAccent) accent else dark)
            x += stripeW
            isAccent = !isAccent
        }
    }
}
